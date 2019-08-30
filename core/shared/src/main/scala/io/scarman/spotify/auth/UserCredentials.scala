package io.scarman.spotify.auth

import java.util.concurrent.atomic.AtomicReference

import com.softwaremill.sttp.circe.asJson
import com.softwaremill.sttp._
import io.scarman.spotify.http._
import io.scarman.spotify.request._
import io.scarman.spotify.response.AccessToken
import scribe._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Left, Right}

// This solution stinks, do not merge!
case class UserCredentials(appId: String, appSecret: String, userAuth: Option[UserAuth] = None)(implicit backend: SttpBackend[Future, Nothing],
                                                                                                execution: ExecutionContext = ExecutionContext.Implicits.global)
  extends Authorization {

  override def explainForbidden(): String = {
    val url = AuthorizationCode(appId, Scopes.All, userAuth.map(_.redirectUri).getOrElse("<redirectUrl>")).authUrl()
    s"Whitelist the redirect URI through your dashboard: https://developer.spotify.com/dashboard/applications\n Then visit this URL to update `code` in Credentials.scala:\n $url"
  }

  private val baseBody = userAuth match {
    case Some(UserAuth(redirectUri, Code(code))) if redirectUri.nonEmpty && code.nonEmpty => List(
      "grant_type" -> "authorization_code",
      "code" -> code,
      "redirect_uri" -> redirectUri
    )
    case _ =>
      List("grant_type" -> "client_credentials")
  }

  lazy private val tokenRef: AtomicReference[Future[AccessToken]] = new AtomicReference[Future[AccessToken]](initToken(appId, appSecret))

  private val baseRequest: Req[AccessToken] = sttp
    .post(tokenUri)
    .contentType(MediaTypes.Form)
    .response(asJson[AccessToken])
    .header("Access-Control-Allow-Origin", "*")
    .body(baseBody: _*)

  private def tokenRequest(req: Req[AccessToken]): Future[AccessToken] = {
    userAuth match {
      case Some(UserAuth(_, Token(token))) if token.nonEmpty =>
        Future.successful(AccessToken(access_token = token,
          token_type = "Bearer",
          expires_in = 3600,
          scope = Some(Scopes.All),
          refresh_token = None,
          state = None))
      case _ =>
        req.send().map(_.body).map {
          case Right(Right(at)) =>
            info(s"Auth Token: ${at.access_token}")
            at
          case Right(Left(err)) =>
            Console.err.println(explainForbidden())
            throw new Exception(s"Can't get auth token: $req\n$err")
          case Left(err) =>
            Console.err.println(explainForbidden())
            throw new Exception(s"Can't get auth token: $req\n$err")
        }
    }
  }

  override protected def initToken(id: String, secret: String): Future[AccessToken] = {
    info(s"$id - $secret")
    val request = baseRequest.auth.basic(id, secret)
    debug(s"Headers: ${request.headers.map { case (k, v) => s"$k=$v" }.mkString(", ")}")
    tokenRequest(request)
  }

  override def refreshToken(): Future[AccessToken] = {
    tokenRef.get().flatMap { t =>
      if (!t.isExpired) {
        Future.successful(t)
      } else {
        tokenRef.getAndSet(getToken)
      }
    }
  }

  override def getToken: Future[AccessToken] = {
    tokenRef.get().flatMap { t =>
      if (t.isExpired) {
        debug("Token expired, requesting new one...")
        tokenRef.getAndSet(refreshToken())
      } else {
        debug(s"Providing Token: ${t.access_token}")
        tokenRef.get()
      }
    }
  }

  override def isExpired: Future[Boolean] = {
    tokenRef.get().map(_.isExpired)
  }

  private case class AuthorizationCode(id: String, scope: String, redirectUri: String, showDialog: Boolean = false) {

    implicit def uriAsString(uri: Uri): String = {
      uri.toString()
    }

    private val params = Map(
      "client_id"     -> id,
      "response_type" -> userAuth.collect{case UserAuth(_, Code(_)) => "code"}.getOrElse("token"),
      "redirect_uri"  -> redirectUri,
      "scope"         -> scope,
      "show_dialog"   -> showDialog
    )

    def authUrl(): String = {
      uri"$authUri/?$params"
    }
  }
}

case class UserAuth(redirectUri: String, codeOrToken: CodeOrToken)
sealed trait CodeOrToken
case class Code(value: String) extends CodeOrToken
case class Token(value: String) extends CodeOrToken

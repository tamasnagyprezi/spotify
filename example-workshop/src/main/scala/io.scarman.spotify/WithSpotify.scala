package io.scarman.spotify

import java.util.concurrent.TimeUnit

import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import io.scarman.spotify.Spotify
import io.scarman.spotify.auth._
import io.scarman.spotify.http.Authorization

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

trait WithSpotify {
  implicit val backend = AsyncHttpClientFutureBackend()

  implicit val creds =  ClientCredentials(Credentials.appId, Credentials.appSecret)
  implicit val spotify = Spotify(creds)
  protected def await[A](future: Future[A]): A =
    Await.result(future, FiniteDuration(15, TimeUnit.SECONDS))
}

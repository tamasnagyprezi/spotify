package io.scarman.spotify

import java.util.concurrent.{Executors, TimeUnit}

import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend
import io.scarman.spotify.auth._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContext, Future}

trait WithSpotify {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(8))
  implicit val backend = AsyncHttpClientFutureBackend()

  implicit val creds = ClientCredentials(Credentials.appId, Credentials.appSecret)
  implicit val spotify = Spotify(creds)

  protected def await[A](future: Future[A]): A =
    Await.result(future, FiniteDuration(15, TimeUnit.SECONDS))
}

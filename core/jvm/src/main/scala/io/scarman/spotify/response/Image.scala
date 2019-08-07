package io.scarman.spotify.response

import com.softwaremill.sttp._
import io.scarman.spotify.http.DownloadResults
import io.scarman.spotify.request.ImageDownload

import scala.concurrent.{ExecutionContext, Future}

case class Image(height: Option[Int], url: String, width: Option[Int]) {

  def download(location: String)(implicit backend: SttpBackend[Future, Nothing], ec: ExecutionContext): Future[DownloadResults] = {
    download(location, None)
  }

  def download(location: String,
               checksum: Option[String])(implicit backend: SttpBackend[Future, Nothing], ec: ExecutionContext): Future[DownloadResults] = {
    ImageDownload(url, location, checksum).download()
  }
}

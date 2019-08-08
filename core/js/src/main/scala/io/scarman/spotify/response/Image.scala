package io.scarman.spotify.response

import com.softwaremill.sttp._
import io.scarman.spotify.http.Authorization
import io.scarman.spotify.request.ImageDownload

import scala.concurrent.Future

case class Image(height: Option[Int], url: String, width: Option[Int]) {

  def download()(implicit auth: Authorization, backend: SttpBackend[Future, Nothing]): ImageDownload = {
    ImageDownload(url, "", None)
  }

}

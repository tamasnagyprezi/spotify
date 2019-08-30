package io.scarman.spotify.request

import com.softwaremill.sttp._
import io.scarman.spotify.http.{Authorization, HttpRequest}
import io.scarman.spotify.{response => r}

import scala.concurrent.Future

/**
  * Get a whole playlist.
  * https://developer.spotify.com/web-api/get-playlist/
  *
  * @param id
  * @param market
  */
case class Playlist(id: String, market: String = "US")(implicit auth: Authorization, backend: SttpBackend[Future, Nothing])
    extends HttpRequest[r.Playlist] {
  lazy protected val reqUri = uri"$base$PL/$id".param("market", market)

  def addTrack(trackIds: List[String]): PlaylistAddTrack = PlaylistAddTrack(id, trackIds)

  def removeTrack(trackIds: List[String]): PlaylistRemoveTrack = PlaylistRemoveTrack(id, trackIds)
}

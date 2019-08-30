package io.scarman.spotify.request

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe.asJson
import io.scarman.spotify.http.{Authorization, HttpRequest, Req}
import io.scarman.spotify.{response => r}

import scala.concurrent.Future

/**
  * Add tracks to a playlist.
  * https://developer.spotify.com/web-api/get-playlist/
  *
  * @param playlist
  * @param trackIds
  * @param market
  */
case class PlaylistAddTrack(playlist: String, trackIds: List[String], market: String = "US")(implicit auth: Authorization, backend: SttpBackend[Future, Nothing])
    extends HttpRequest[r.PlaylistSnapshotId] {
  override lazy protected val reqUri =
    uri"$base$PL/$playlist/tracks"
    .param("market", market)
    .param("uris", trackIds.mkString(","))
  override protected val request: Req[r.PlaylistSnapshotId] = sttp.post(reqUri).response(asJson[r.PlaylistSnapshotId])
}

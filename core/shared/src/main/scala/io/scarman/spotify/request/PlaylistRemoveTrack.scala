package io.scarman.spotify.request

import com.softwaremill.sttp._
import com.softwaremill.sttp.circe._
import io.scarman.spotify.http.{Authorization, HttpRequest, Req}
import io.scarman.spotify.{response => r}

import scala.concurrent.Future

/**
 * Add tracks to a playlist.
 * https://developer.spotify.com/web-api/remove-tracks-playlist/
 *
 * @param playlist
 * @param trackIds
 * @param market
 */
case class PlaylistRemoveTrack(playlist: String, trackIds: List[String], market: String = "US")(implicit auth: Authorization, backend: SttpBackend[Future, Nothing])
  extends HttpRequest[r.PlaylistSnapshotId] {

  override val reqUri: Uri = uri"$base$PL/$playlist/tracks"

  override val request: Req[r.PlaylistSnapshotId] = sttp
    .body(trackIds.map(t => s"""{"uri":"$t"}""").mkString("""{"tracks":[""", ",", "]}"))
    .contentType("application/json")
    .delete(reqUri)
    .response(asJson[r.PlaylistSnapshotId])
}

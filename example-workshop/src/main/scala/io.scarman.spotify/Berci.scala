package io.scarman.spotify

import io.scarman.spotify.Berci.TrackWithRelatedArtists
import io.scarman.spotify.request.Playlist

import scala.concurrent.Future

class Berci extends WithSpotify {

  def run(): Unit = {

    val myPlaylistRequest = Playlist("31GOsSXZrkUQgULwizcj2O")
    val datawingRequest = Playlist("3vljBAykhTtxl5ZZv8zuua")

    val artists = for {
      myTracks <- getTracksWithRelatedArtists(myPlaylistRequest)
      datawingTracks <- getTracksWithRelatedArtists(datawingRequest)
    } yield comparePlaylistTracks(myTracks, datawingTracks)

    await(artists)
  }

  def comparePlaylistTracks(l1: List[TrackWithRelatedArtists], l2: List[TrackWithRelatedArtists]) = {
    for {
      t1 <- l1
      t2 <- l2
    } {
      val t1Artists = (t1.track.track.artists ::: t1.related).map(_.name).toSet
      val t2Artists = (t2.track.track.artists ::: t2.related).map(_.name).toSet

      val similarArtists = t1Artists.intersect(t2Artists)

      if (similarArtists.nonEmpty) {
        println(s"Similar: ${t1.track.track.name} and ${t2.track.track.name}: $similarArtists")
      }
    }
  }

  def getTracksWithRelatedArtists(playlistRequest: Playlist): Future[List[TrackWithRelatedArtists]] = for {
    playlist <- playlistRequest()
    tracks = playlist.tracks.items
    relatedArtists <- Future.sequence(tracks.map(t => getRelatedArtists(t.track.artists)))
  } yield tracks.zip(relatedArtists).map(p => TrackWithRelatedArtists(p._1,p._2))

  def getRelatedArtists(artists: List[io.scarman.spotify.response.Artist]): Future[List[io.scarman.spotify.response.Artist]] =
    Future.sequence(artists.map(a => Artist(a.id).relatedArtists()())).map { as =>
      as.flatMap(a => a.artists)
    }
}

object Berci {
  case class TrackWithRelatedArtists(track: io.scarman.spotify.response.PlaylistTrack,
                                     related: List[io.scarman.spotify.response.Artist])
}

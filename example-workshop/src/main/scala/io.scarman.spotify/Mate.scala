package io.scarman.spotify

import io.scarman.spotify.response.PlaylistTrack

import scala.concurrent.Future

class Mate extends WithSpotify {

  case class TrackWithFeatures(track: PlaylistTrack, features: response.AudioFeatures)

  def run(): Unit = {
    // val babyLullaby = Playlist("3YfIjWjxZbjxS2NiOOkpve")
    val babyLullaby = Playlist("37i9dQZF1DX8Sz1gsYZdwj")
    val futResult = babyLullaby().flatMap { list =>
      println(list.name)
      val tracks = list.tracks.items.sortBy(_.track.popularity)

      Future.sequence {
        tracks.map { ptrack =>
          val track = Track(ptrack.track.id)
          track.getAudioFeatures().map { features =>
            TrackWithFeatures(ptrack, features)
          }
        }
      }
    }

    await(futResult)
      .sortBy(_.features.tempo)
      .map(r => s"${r.features.tempo} ${r.track.track.name}")
      .foreach(println)
  }

}

package io.scarman.spotify

import io.scarman.spotify.request.Playlist
import io.scarman.spotify.response.PlaylistTrack

import scala.concurrent.Future

class Mate extends WithSpotify {

  case class TrackWithFeatures(track: PlaylistTrack, features: response.AudioFeatures) {
    def compareWith(top: TrackWithFeatures): Double =
      Math.abs(top.features.energy - features.energy) +
        Math.abs(top.features.acousticness - features.acousticness) +
        Math.abs(top.features.liveness - features.liveness) +
        Math.abs(top.features.valence - features.valence)

    override def toString: String = s"${track.track.name} energy:${features.energy} acousticness:${features.acousticness} liveness:${features.liveness} valence:${features.valence} "
  }

  case class TrackWithAnalysis(track: PlaylistTrack, analysis: response.AudioAnalysis)

  case class TrackWithFeaturesAndAnalysis(track: PlaylistTrack, features: response.AudioFeatures, analysis: response.AudioAnalysis)

  implicit class PlaylistTrackAdditions(playlistTrack: PlaylistTrack) {
    def getTrack = Track(playlistTrack.track.id)

    def getFeatures: Future[TrackWithFeatures] = {
      getTrack.getAudioFeatures().map { features =>
        TrackWithFeatures(playlistTrack, features)
      }
    }

    def getAnalysis: Future[TrackWithAnalysis] = {
      getTrack.getAudioAnalysis().map { analysis =>
        TrackWithAnalysis(playlistTrack, analysis)
      }
    }

    def getFeaturesAndAnalysis: Future[TrackWithFeaturesAndAnalysis] = {
      val track = playlistTrack.getTrack
      for {
        features <- track.getAudioFeatures()
        analysis <- track.getAudioAnalysis()
      } yield TrackWithFeaturesAndAnalysis(playlistTrack, features, analysis)
    }
  }

  def orderingByTrackWithFeatures(track: TrackWithFeatures): Ordering[TrackWithFeatures] = {
    Ordering.by { t => t.compareWith(track) }
  }

  def run(): Unit = {
    val playlists: Seq[Playlist] = List(
      Playlist("37i9dQZF1DX8Sz1gsYZdwj")
    )
    val bestSongId = "2QEcIFrrwOyCHgV5UqyrGe"

    val targetPlaylist: Playlist = Playlist("4MBTtt7gaLiPlSkoXetUsh")

    val futResult = for {
      playlists <- Future.sequence(playlists.map(_.apply()))
      _ = println(playlists.map(_.name))
      tracks = playlists.flatMap(_.tracks.items).distinctBy(_.track.id)
      tracksWithFeatures <- Future.sequence(tracks.map(_.getFeatures))
      bestSong = tracksWithFeatures.find(_.track.track.id == bestSongId).get
      ordering = orderingByTrackWithFeatures(bestSong)
      sortedResult = tracksWithFeatures.sorted(ordering)
      _ = sortedResult.foreach(println)
      _ <- targetPlaylist.removeTrack(tracks.map(_.track.uri).toList).apply()
      addTopTracks <- targetPlaylist.addTrack(sortedResult.take(4).map(_.track.track.uri).toList).apply()
    } yield addTopTracks

    await(futResult)
  }

}

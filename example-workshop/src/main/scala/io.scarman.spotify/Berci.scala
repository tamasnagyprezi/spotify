package io.scarman.spotify

import scala.concurrent.Future

class Berci extends WithSpotify {

  def run(): Unit = {

    val myPlaylist = Playlist("31GOsSXZrkUQgULwizcj2O")
    val datawing = Playlist("5cNp1xS22nNOwVtLMIm8og")

    // WARNING: Be careful with rewriting this as it can result in quickly exceeding the rate limits
    val artistLimit = 1

    val artists = for {
      myTracks <- myPlaylist()
      datawingTracks <- datawing()
      myArtists = myTracks.tracks.items.flatMap(_.track.artists).distinct
      myRelatedArtists <- getRelatedArtists(myArtists.take(artistLimit))
      moreRelatedArtists <- getRelatedArtists(myRelatedArtists.take(artistLimit))
      datawingArtists = datawingTracks.tracks.items.flatMap(_.track.artists).distinct
      matchingArtists = (myArtists ::: myRelatedArtists ::: moreRelatedArtists).distinct
        .filter(a => datawingArtists.map(_.id).contains(a.id))
    } yield matchingArtists

    await(artists).foreach(a => println(a.name))
  }

  def getRelatedArtists(artists: List[io.scarman.spotify.response.Artist]): Future[List[io.scarman.spotify.response.Artist]] =
    Future.sequence(artists.map(a => Artist(a.id).relatedArtists()())).map { as =>
      as.flatMap(a => a.artists)
    }
}

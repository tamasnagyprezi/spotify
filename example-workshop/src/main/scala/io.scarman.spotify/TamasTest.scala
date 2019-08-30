package io.scarman.spotify

import scala.concurrent.Future

class TamasTest extends WithSpotify {
  def wip(): Unit = {
    val barthezz = Artist("2ikrIhCYSKmzCgWb9EM3aT")
    await(barthezz.topTracks()()).tracks.map(_.name).foreach(println)

    val oneWithYou = Track("3eA6NakkA6fFaMTw1sTYZ5")
    println(await(oneWithYou.getAudioAnalysis()))
    println(await(oneWithYou.getAudioFeatures()).valence)

    val apacukaFriday = Playlist("3YfIjWjxZbjxS2NiOOkpve")
    val list          = await(apacukaFriday())
    println(list.name)
    val sorted = list.tracks.items.map(t => (t.track.name, t.track.popularity))
    sorted.sortBy(_._2).foreach(println)

    val berci = Playlist("5cNp1xS22nNOwVtLMIm8og")
    println(await(berci()))
  }

  def getRelatedArtists(playlist: request.Playlist): Set[String] = {
    val list       = await(playlist())
    val artists    = list.tracks.items.flatMap(t => t.track.artists.map(_.id))
    val getRelated = artists.map(Artist(_).relatedArtists()().map(_.artists.map(_.id)))
    await(Future.sequence(getRelated)).flatten.toSet
  }

  def shuffle(playlist: request.PlaylistAddTrack): Unit = {
    await(playlist())
  }

  def run0(): Unit = {
    val relatedToMonday = getRelatedArtists(Playlist("1M0w0CabcTplJJGyfbDPKG"))
    println(relatedToMonday)

    val friday = Playlist("3YfIjWjxZbjxS2NiOOkpve")
    val from   = await(friday())
    from.tracks.items.collect {
      case t if relatedToMonday.intersect(t.track.artists.map(_.id).toSet).nonEmpty =>
        println(t.track.name)
    }
  }

  def run(): Unit = {
    val thursday = Playlist("3CDyh6RFvALszUNUlbQh9D")
    val addEdge = thursday.addTrack(List("spotify:track:2AJmSDr26ZvsunbofYzVqG"))
    println(await(addEdge()))
  }
}

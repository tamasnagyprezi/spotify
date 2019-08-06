package io.scarman.spotify

import scala.util.control.NonFatal
import io.scarman.spotify._

object Main extends App {
  try {
    new ArtistTest().run()
    System.exit(0)
  } catch {
    case NonFatal(e) =>
      println(e)
      System.exit(1)
  }
}

class ArtistTest extends WithSpotify {
  def run(): Unit = {
    val barthezz = Artist("2ikrIhCYSKmzCgWb9EM3aT")
    await(barthezz.topTracks()()).tracks.map(_.name).foreach(println)

    val oneWithYou = Track("3eA6NakkA6fFaMTw1sTYZ5")
    println(await(oneWithYou.getAudioAnalysis()))
    println(await(oneWithYou.getAudioFeatures()))

    val apacukaFriday = Playlist("3YfIjWjxZbjxS2NiOOkpve")
    val list = await(apacukaFriday())
    println(list.name)
    list.tracks.items.map(t => (t.track.name, t.track.popularity)).foreach(println)
  }
}

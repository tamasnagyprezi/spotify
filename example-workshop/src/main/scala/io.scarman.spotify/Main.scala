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
    val artist = Artist("2ikrIhCYSKmzCgWb9EM3aT")
    await(artist.topTracks()()).tracks.map(_.name).foreach(println)

    val oneWithYou = Track("3eA6NakkA6fFaMTw1sTYZ5")
    println(await(oneWithYou.getAudioAnalysis()))
    println(await(oneWithYou.getAudioFeatures()))
  }
}

package io.scarman.spotify

import scala.util.control.NonFatal

object Main extends App {
  try {
    new TamasTest().run()
    System.exit(0)
  } catch {
    case NonFatal(e) =>
      println(e)
      System.exit(1)
  }
}

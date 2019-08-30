package io.scarman.spotify

import io.scarman.spotify.auth.{Token, UserAuth}

object Credentials {
  val appId = ""
  val appSecret = ""
  val userAuth = UserAuth(
    redirectUri = "http://localhost:8080/index-dev.html",
    Token("")
  )
}

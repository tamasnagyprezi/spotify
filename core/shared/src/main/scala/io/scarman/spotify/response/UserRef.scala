package io.scarman.spotify.response

case class UserRef(
    external_urls: ExternalUrl,
    href: String,
    id: String,
    `type`: String,
    uri: String
)

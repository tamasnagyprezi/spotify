package io.scarman.spotify.response

case class Playlist(collaborative: Boolean,
                    description: String,
                    external_urls: ExternalUrl,
                    followers: Followers,
                    href: String,
                    id: String,
                    images: List[Image],
                    name: String,
                    owner: UserRef,
                    primary_color: Option[String],
                    public: Option[Boolean],
                    snapshot_id: String,
                    tracks: PlaylistTracks,
                    `type`: String,
                    uri: String)

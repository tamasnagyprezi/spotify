package io.scarman.spotify.response

import io.scarman.spotify.util.SongDuration

case class PlaylistTrack(
    added_at: String,
    added_by: UserRef,
    is_local: Boolean,
    primary_color: Option[String],
    track: Track)

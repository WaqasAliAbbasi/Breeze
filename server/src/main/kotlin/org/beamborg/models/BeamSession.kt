package org.beamborg.models

import kotlinx.serialization.Serializable

enum class BeamSessionContentType { Image, Text }
@Serializable
data class BeamSession(val id: String, val type: BeamSessionContentType? = null, val content: String? = null)

val beamSessionsStorage = mutableListOf<BeamSession>()

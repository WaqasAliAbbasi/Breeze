package org.beamborg.models

import kotlinx.serialization.Serializable

@Serializable
data class BeamSession(val id: String, val content: String? = null)

val beamSessionsStorage = mutableListOf<BeamSession>()

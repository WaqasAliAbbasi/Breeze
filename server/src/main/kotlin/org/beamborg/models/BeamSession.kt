package org.beamborg.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

enum class BeamSessionContentType { Image, Text }
@Serializable
data class BeamSession(val id: String, val type: BeamSessionContentType? = null, val content: String? = null)

const val TEN_MEGABYTES = 1024 * 1024 * 10
object BeamSessions : Table() {
    val id = varchar("id", 128)
    val type = enumerationByName("type", 128, BeamSessionContentType::class).nullable()
    val content = varchar("content", TEN_MEGABYTES).nullable()

    override val primaryKey = PrimaryKey(id)
}
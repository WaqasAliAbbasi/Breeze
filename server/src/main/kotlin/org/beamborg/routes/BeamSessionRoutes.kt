package org.beamborg.routes

import io.ktor.server.routing.*
import org.beamborg.models.beamSessionsStorage

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.beamborg.models.BeamSession
import org.beamborg.models.BeamSessionContentType
import java.util.*

@Serializable
data class BeamSessionUpdate(val content: String)

private fun updateBeamSessionContent(id: String, type: BeamSessionContentType, content: String): BeamSession {
    val beamSession =
        beamSessionsStorage.find { it.id == id }!!
    val modifiedBeamSession = beamSession.copy(type = type, content = content)
    beamSessionsStorage[beamSessionsStorage.indexOf(beamSession)] = modifiedBeamSession
    return modifiedBeamSession
}

fun Route.beamSessionRouting() {
    route("/session") {
        get {
            if (beamSessionsStorage.isNotEmpty()) {
                call.respond(beamSessionsStorage)
            } else {
                call.respondText("No sessions found", status = HttpStatusCode.OK)
            }
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val beamSession =
                beamSessionsStorage.find { it.id == id } ?: return@get call.respondText(
                    "No session with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(beamSession)
        }
        post("/new") {
            val beamSession = BeamSession(id=java.util.UUID.randomUUID().toString().slice(IntRange(0,7)))
            beamSessionsStorage.add(beamSession)
            call.respond(HttpStatusCode.Created, beamSession)
        }
        post {
            val beamSession = call.receive<BeamSession>()
            beamSessionsStorage.add(beamSession)
            call.respondText("Session stored correctly", status = HttpStatusCode.Created)
        }
        post("{id?}/upload") {
            val id = call.parameters["id"] ?: return@post call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val multipartData = call.receiveMultipart()
            val parts = multipartData.readAllParts()
            if (parts.size != 1) {
                return@post call.respondText("Invalid number of files", status = HttpStatusCode.BadRequest)
            }

            beamSessionsStorage.find { it.id == id } ?: return@post call.respondText(
                "No session with id $id",
                status = HttpStatusCode.NotFound
            )

            when (val part = parts[0]) {
                is PartData.FormItem -> {
                    val text = part.value
                    updateBeamSessionContent(id, BeamSessionContentType.Text, text)
                }
                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val base64Encoded = Base64.getEncoder().withoutPadding().encodeToString(fileBytes)
                    updateBeamSessionContent(id, BeamSessionContentType.Image, base64Encoded)
                }
                else -> return@post call.respondText("Unknown file type", status = HttpStatusCode.BadRequest)
            }
            call.respondText("Uploaded")
        }
    }
}


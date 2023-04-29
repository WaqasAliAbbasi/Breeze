package org.beamborg.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.beamborg.dao.BeamSessionRepository
import org.beamborg.models.BeamSessionContentType
import org.koin.ktor.ext.inject
import java.util.*

fun Route.beamSessionRouting() {
    val repository by inject<BeamSessionRepository>()

    route("/session") {
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val beamSession =
                repository.beamSession(id) ?: return@get call.respondText(
                    "No session with id $id",
                    status = HttpStatusCode.NotFound
                )
            call.respond(beamSession)
        }
        post("/new") {
            val beamSession = repository.addNewBeamSession(
                id = UUID.randomUUID().toString().slice(IntRange(0, 7)),
                type = null,
                content = null
            )
            call.respond(HttpStatusCode.Created, beamSession)
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

            repository.beamSession(id) ?: return@post call.respondText(
                "No session with id $id",
                status = HttpStatusCode.NotFound
            )

            when (val part = parts[0]) {
                is PartData.FormItem -> {
                    val text = part.value
                    repository.editBeamSession(id, BeamSessionContentType.Text, text)
                }

                is PartData.FileItem -> {
                    val fileBytes = part.streamProvider().readBytes()
                    val base64Encoded = Base64.getEncoder().withoutPadding().encodeToString(fileBytes)
                    repository.editBeamSession(id, BeamSessionContentType.Image, base64Encoded)
                }

                else -> return@post call.respondText("Unknown file type", status = HttpStatusCode.BadRequest)
            }
            call.respondText("Uploaded")
        }
    }
}


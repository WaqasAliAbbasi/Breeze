package org.beamborg.routes

import io.ktor.server.routing.*
import org.beamborg.models.beamSessionsStorage

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.beamborg.models.BeamSession

@Serializable
data class BeamSessionUpdate(val content: String)


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
        patch("{id?}") {
            val id = call.parameters["id"] ?: return@patch call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val beamSession =
                beamSessionsStorage.find { it.id == id } ?: return@patch call.respondText(
                    "No session with id $id",
                    status = HttpStatusCode.NotFound
                )

            val beamSessionInput = call.receive<BeamSessionUpdate>()

            val modifiedBeamSession = beamSession.copy(content = beamSessionInput.content)

            beamSessionsStorage[beamSessionsStorage.indexOf(beamSession)] = modifiedBeamSession

            call.respond(HttpStatusCode.OK, modifiedBeamSession)
        }
    }
}


package org.beamborg.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import kotlinx.serialization.Serializable
import org.beamborg.models.BeamSession
import org.beamborg.routes.beamSessionRouting

fun Application.configureRouting() {
    routing {
        beamSessionRouting()
        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
        get("/hello") {
            call.respondText("Hello World!")
        }
    }
}

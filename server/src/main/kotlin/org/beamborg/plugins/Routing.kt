package org.beamborg.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.beamborg.routes.beamSessionRouting

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
            beamSessionRouting()
        }
    }
}

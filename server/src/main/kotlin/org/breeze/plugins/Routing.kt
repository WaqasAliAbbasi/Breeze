package org.breeze.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.breeze.routes.beamSessionRouting

fun Application.configureRouting() {
    routing { route("/api/v1") { beamSessionRouting() } }
}

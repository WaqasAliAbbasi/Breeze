package org.beamborg.plugins

import io.ktor.http.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
    routing {
        swaggerUI(path = "openapi")
    }
}

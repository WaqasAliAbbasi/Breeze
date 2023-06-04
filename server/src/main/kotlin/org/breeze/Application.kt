package org.breeze

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.breeze.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
}

fun Application.module() {
    configureDatabase()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureDependencyInjection()
    configureRouting()
}

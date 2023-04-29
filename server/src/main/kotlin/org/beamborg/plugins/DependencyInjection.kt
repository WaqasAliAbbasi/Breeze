package org.beamborg.plugins

import io.ktor.server.application.*
import org.beamborg.dao.BeamSessionRepository
import org.beamborg.dao.BeamSessionRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

val appModule = module {
    singleOf(::BeamSessionRepositoryImpl) { bind<BeamSessionRepository>() }
}

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
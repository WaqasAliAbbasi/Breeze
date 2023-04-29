package org.beamborg.plugins

import org.beamborg.dao.DatabaseFactory

fun configureDatabase() {
    DatabaseFactory.init()
}

package org.breeze.plugins

import org.breeze.dao.DatabaseFactory

fun configureDatabase() {
    DatabaseFactory.init()
}

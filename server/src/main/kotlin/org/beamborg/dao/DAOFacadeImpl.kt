package org.beamborg.dao

import org.beamborg.dao.DatabaseFactory.dbQuery
import org.beamborg.models.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
class DAOFacadeImpl : DAOFacade {
    private fun resultRowToArticle(row: ResultRow) = BeamSession(
        id = row[BeamSessions.id],
        type = row[BeamSessions.type],
        content = row[BeamSessions.content],
    )

    override suspend fun allBeamSessions(): List<BeamSession> = dbQuery {
        BeamSessions.selectAll().map(::resultRowToArticle)
    }

    override suspend fun beamSession(id: String): BeamSession? = dbQuery {
        BeamSessions
            .select { BeamSessions.id.eq(id) }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewBeamSession(id: String, type: BeamSessionContentType?, content: String?): BeamSession = dbQuery {
        val insertStatement = BeamSessions.insert {
            it[BeamSessions.id] = id
            it[BeamSessions.type] = type
            it[BeamSessions.content] = content
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)!!
    }

    override suspend fun editBeamSession(id: String, type: BeamSessionContentType, content: String) = dbQuery {
        BeamSessions.update({ BeamSessions.id eq id }) {
            it[BeamSessions.type] = type
            it[BeamSessions.content] = content
        } > 0
    }

    override suspend fun deleteBeamSession(id: String): Boolean = dbQuery {
        BeamSessions.deleteWhere { BeamSessions.id eq id } > 0
    }

    override suspend fun deleteAllBeamSessions(): Boolean = dbQuery {
        BeamSessions.deleteAll() > 0
    }
}
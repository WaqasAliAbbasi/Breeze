package org.beamborg

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.beamborg.models.BeamSession
import org.beamborg.models.BeamSessionContentType
import org.beamborg.models.beamSessionsStorage

import org.beamborg.plugins.*

class ApplicationTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun testHello() = testApplication {
        application {
            configureRouting()
        }

        client.get("/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testNewBeamSession() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        client.post("/api/v1/session/new").apply {
            val receivedResponse = json.decodeFromString<BeamSession>(bodyAsText())
            assertEquals(HttpStatusCode.Created, status)
            assert(receivedResponse.id.isNotEmpty())
        }
    }

    @Test
    fun testGetBeamSession() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val expectedSession = BeamSession(id="123", type=BeamSessionContentType.Text, content="Something Random")
        beamSessionsStorage.add(expectedSession)
        client.get("/api/v1/session/123").apply {
            val expectedResponse = json.encodeToString(BeamSession.serializer(), expectedSession)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(expectedResponse, body())
        }
    }
}

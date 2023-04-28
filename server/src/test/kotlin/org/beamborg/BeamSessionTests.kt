package org.beamborg

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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
import java.io.File
import java.net.URL

class BeamSessionTests {
    private val json = Json { encodeDefaults = true }
    private val existingSession = BeamSession(id="123", type=BeamSessionContentType.Text, content="Something Random")
    @BeforeTest
    fun beforeTest() {
        beamSessionsStorage.clear()
        beamSessionsStorage.add(existingSession)
    }

    @Test
    fun testNew() = testApplication {
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
    fun testGet() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        client.get("/api/v1/session/123").apply {
            val expectedResponse = json.encodeToString(BeamSession.serializer(), existingSession)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(expectedResponse, body())
        }
    }

    @Test
    fun testGetNonExistent() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }

        client.get("/api/v1/session/007").apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("No session with id 007", body())
        }
    }

    @Test
    fun testPostText() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val existingSession = BeamSession(id="123")
        beamSessionsStorage.add(existingSession)
        val response = client.post("/api/v1/session/123/upload") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("text", "Some new content")
                },
                boundary = "WebAppBoundary"
            ))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Uploaded", response.body())
        assertEquals(beamSessionsStorage[0], BeamSession(id="123", type=BeamSessionContentType.Text, content="Some new content"))
    }

    @Test
    fun testPostImage() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val existingSession = BeamSession(id="123")
        beamSessionsStorage.add(existingSession)
        val testImage = File({}.javaClass.getResource("/test.png")?.file!!)
        val response = client.post("/api/v1/session/123/upload") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("image",testImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"test.png\"")
                    })
                },
                boundary = "WebAppBoundary"
            ))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Uploaded", response.body())
        assertEquals(beamSessionsStorage[0].type, BeamSessionContentType.Image)
        assertEquals(beamSessionsStorage[0].content?.isNotEmpty(),true)
    }

    @Test
    fun testPostImageTextBothInvalid() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val existingSession = BeamSession(id="123")
        beamSessionsStorage.add(existingSession)
        val testImage = File({}.javaClass.getResource("/test.png")?.file!!)
        val response = client.post("/api/v1/session/123/upload") {
            setBody(MultiPartFormDataContent(
                formData {
                    append("text", "Some new content")
                    append("image",testImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"test.png\"")
                    })
                },
                boundary = "WebAppBoundary"
            ))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid number of files", response.body())
    }
}

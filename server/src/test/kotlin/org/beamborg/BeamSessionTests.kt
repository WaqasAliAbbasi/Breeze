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
import org.beamborg.dao.DAOFacade
import org.beamborg.dao.DAOFacadeImpl
import org.beamborg.models.BeamSession
import org.beamborg.models.BeamSessionContentType

import org.beamborg.plugins.*
import java.io.File

class BeamSessionTests {
    private val json = Json { encodeDefaults = true }
    private val existingSession = BeamSession(id="123")
    private var dao: DAOFacade
    init {
        configureDatabase()
        dao = DAOFacadeImpl()
    }

    private suspend fun beforeTest() {
        dao.deleteAllBeamSessions()
        dao.addNewBeamSession(existingSession.id, existingSession.type, existingSession.content)
    }

    @Test
    fun testNew() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        beforeTest()
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
        beforeTest()
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
        beforeTest()
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
        beforeTest()
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
        assertEquals(dao.beamSession("123"), BeamSession(id="123", type=BeamSessionContentType.Text, content="Some new content"))
    }

    @Test
    fun testPostImage() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        beforeTest()
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
        val beamSession = dao.beamSession("123")
        assertEquals(beamSession?.type, BeamSessionContentType.Image)
        assertEquals(beamSession?.content?.isNotEmpty(),true)
    }

    @Test
    fun testPostImageTextBothInvalid() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        beforeTest()
        val testImage = File({}.javaClass.getResource("/test.png")?.file!!)
        val response = client.post("/api/v1/session/123/upload") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("text", "Some new content")
                        append("image", testImage.readBytes(), Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(HttpHeaders.ContentDisposition, "filename=\"test.png\"")
                        })
                    },
                    boundary = "WebAppBoundary"
                )
            )
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid number of files", response.body())
    }
}

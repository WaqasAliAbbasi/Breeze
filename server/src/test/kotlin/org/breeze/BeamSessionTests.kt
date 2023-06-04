package org.breeze

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.breeze.dao.BeamSessionRepository
import org.breeze.models.BeamSession
import org.breeze.models.BeamSessionContentType
import org.breeze.plugins.appModule
import org.breeze.plugins.configureDatabase
import org.breeze.plugins.configureRouting
import org.breeze.plugins.configureSerialization
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class BeamSessionTests : KoinTest {
    private val json = Json { encodeDefaults = true }
    private val existingSession = BeamSession(id = "123")
    private val repository: BeamSessionRepository by inject()

    init {
        configureDatabase()
    }

    @BeforeTest
    fun setup() {
        startKoin { modules(appModule) }
    }

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    private suspend fun beforeTest() {
        repository.deleteAllBeamSessions()
        repository.addNewBeamSession(
                existingSession.id,
                existingSession.type,
                existingSession.content
        )
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
        val response =
                client.post("/api/v1/session/123/upload") {
                    setBody(
                            MultiPartFormDataContent(
                                    formData { append("text", "Some new content") },
                                    boundary = "WebAppBoundary"
                            )
                    )
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Uploaded", response.body())
        assertEquals(
                repository.beamSession("123"),
                BeamSession(
                        id = "123",
                        type = BeamSessionContentType.Text,
                        content = "Some new content"
                )
        )
    }

    @Test
    fun testPostImage() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        beforeTest()
        val testImage = File({}.javaClass.getResource("/test.png")?.file!!)
        val response =
                client.post("/api/v1/session/123/upload") {
                    setBody(
                            MultiPartFormDataContent(
                                    formData {
                                        append(
                                                "image",
                                                testImage.readBytes(),
                                                Headers.build {
                                                    append(HttpHeaders.ContentType, "image/png")
                                                    append(
                                                            HttpHeaders.ContentDisposition,
                                                            "filename=\"test.png\""
                                                    )
                                                }
                                        )
                                    },
                                    boundary = "WebAppBoundary"
                            )
                    )
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Uploaded", response.body())
        val beamSession = repository.beamSession("123")
        assertEquals(beamSession?.type, BeamSessionContentType.Image)
        assertEquals(beamSession?.content?.isNotEmpty(), true)
    }

    @Test
    fun testPostImageTextBothInvalid() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        beforeTest()
        val testImage = File({}.javaClass.getResource("/test.png")?.file!!)
        val response =
                client.post("/api/v1/session/123/upload") {
                    setBody(
                            MultiPartFormDataContent(
                                    formData {
                                        append("text", "Some new content")
                                        append(
                                                "image",
                                                testImage.readBytes(),
                                                Headers.build {
                                                    append(HttpHeaders.ContentType, "image/png")
                                                    append(
                                                            HttpHeaders.ContentDisposition,
                                                            "filename=\"test.png\""
                                                    )
                                                }
                                        )
                                    },
                                    boundary = "WebAppBoundary"
                            )
                    )
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid number of files", response.body())
    }
}

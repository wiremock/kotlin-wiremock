package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import java.net.ServerSocket

class JUnit4ExampleTest {

    val port = findRandomPort()

    val url = "http://localhost:$port"

    @Rule
    @JvmField
    var wiremock: WireMockRule = WireMockRule(port)

    @Test
    fun `url matching`() {
        wiremock.get {
            url like  "/users/.*"
        } returns  {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = """
            {
              "id": 1,
              "name": "Bob"
            }
            """
        }
        assertBobSuccess()
    }

    private fun assertBobSuccess() {
        When {
            get("$url/users/1")
        } Then {
            statusCode(200)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    fun findRandomPort(): Int {
        ServerSocket(0).use { socket -> return socket.localPort }
    }
}
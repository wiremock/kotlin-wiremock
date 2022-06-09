package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.VerificationException
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VerificationTest : AbstractTest() {

    @Test
    fun `multiple verifications can be called individually or together`() {
        setupWiremockAndCallIt()

        wiremock.verify { get(exactly = 1) }
        wiremock.verify { get("/hello") }
        wiremock.verify {
            get("/hello", atLeast = 1)
            get("/hello", atMost = 1)
            get("/hello", exactly = 1)
            get("/hello", atLeast = 0, atMost = 10)
        }
    }


    @Test
    fun `count verifications can be called should fail when incorrect`() {
        setupWiremockAndCallIt()

        assertThrows<VerificationException> { wiremock.verify { get("/hello", atLeast = 2) } }
        assertThrows<VerificationException> { wiremock.verify { get("/hello", atMost = 0) } }
        assertThrows<VerificationException> { wiremock.verify { get("/hello", exactly = 4) } }
        assertThrows<VerificationException> { wiremock.verify { get("/hello", atLeast = 3, atMost = 10) } }
    }

    @Test
    fun `atLeast must be less than atMost`() {
        assertThrows<IllegalArgumentException> { wiremock.verify { get("/hello", atLeast = 10, atMost = 1) } }
    }

    @Test
    fun `response with two headers`() {
        setupWiremockAndCallIt()

//        wiremock.verify(1, getRequestedFor(urlEqualTo("/hello")))

        wiremock.verify {
            get("/hello", atLeast = 1)
            assertThrows<IllegalArgumentException> { get(atLeast = 5, atMost = 2) }
        }
    }

    private fun setupWiremockAndCallIt() {
        wiremock.get {
            url equalTo "/hello"
        } returns {
            statusCode = 200
            header = "TraceId" to "abcd123"
            header = "ExecutionTime" to "10"
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
            header("TraceId", "abcd123")
            header("ExecutionTime", "10")
        }
    }

}

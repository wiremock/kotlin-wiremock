package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class RequestHeadersTest : AbstractTest() {
    @Test
    fun `headers contains negative`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "any"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `headers contains positive`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "User-Agent"
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "whatever")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `headers contains equalTo negative`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "User-Agent" equalTo "curl"
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "whatever")
        } When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `headers contains equalTo positive`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "User-Agent" equalTo "curl"
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `headers contains like positive`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "User-Agent" like "cu.*"
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `headers contains like negative`() {
        wiremock.get {
            url equalTo "/hello"
            headers contains "User-Agent" like "cur.."
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }
}

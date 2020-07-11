package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class RequestCookiesTest : AbstractTest() {
    @Test
    fun `cookies contains negative`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "any"
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
    fun `cookies contains positive`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "User-Agent"
        } returns {
            statusCode = 200
        }

        Given {
            cookie("User-Agent", "whatever")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `cookies contains equalTo negative`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "User-Agent" equalTo "curl"
        } returns {
            statusCode = 200
        }

        Given {
            cookie("User-Agent", "whatever")
        } When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `cookies contains equalTo positive`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "User-Agent" equalTo "curl"
        } returns {
            statusCode = 200
        }

        Given {
            cookie("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `cookies contains like positive`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "User-Agent" like "cu.*"
        } returns {
            statusCode = 200
        }

        Given {
            cookie("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `cookies contains like negative`() {
        wiremock.get {
            url equalTo "/hello"
            cookies contains "User-Agent" like "cur.."
        } returns {
            statusCode = 200
        }

        Given {
            cookie("User-Agent", "curl")
        } When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }
}

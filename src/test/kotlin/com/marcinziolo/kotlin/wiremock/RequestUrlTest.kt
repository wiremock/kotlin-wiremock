package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test


class RequestUrlTest : AbstractTest() {
    @Test
    fun `url equalTo`() {
        wiremock.get {
            url equalTo "/hello"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `url notLike`() {
        wiremock.get {
            url notLike "/hello2"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `url like`() {
        wiremock.get {
            url like "/hel.*"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `url contains`() {
        wiremock.get {
            url contains "hel"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }
}

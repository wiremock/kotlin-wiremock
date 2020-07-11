package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


class AndTest : AbstractTest() {
    @Test
    fun `request and`() {
        wiremock.get {
            url equalTo "/hello"
        } and {
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
    fun `response and`() {
        wiremock.get {
            url equalTo "/hello"
        } returns {
            header = "Content-Type" to "application/json"
            body = """
                {"pet": "dog"}
            """
        } and {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
            body("pet", equalTo("dog"))
        }
    }
}

package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ResponseTest : AbstractTest() {
    @Test
    fun `response with two headers`() {
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

    @Test
    fun `returns json`() {
        wiremock.get {
            url equalTo "/hello"
        } returnsJson {
            body = """
                {"pet": "dog"}
            """
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
            body("pet", equalTo("dog"))
        }
    }
}

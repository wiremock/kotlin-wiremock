package com.marcinziolo.kotlin.wiremock.otherPackage

import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.marcinziolo.kotlin.wiremock.*
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WithBuilderTest : AbstractTest() {
    @Test
    fun `response with two headers`() {
        wiremock.get {
            url equalTo "/hello"
            withBuilder {
                withHeader("SessionId", matching("1234"))
            }
        } returns {
            statusCode = 200
            header = "TraceId" to "abcd123"
            header = "ExecutionTime" to "10"
        }

        Given {
          headers("SessionId", "1234")
        } When {
            get("$url/hello")


        } Then {
            statusCode(200)
            header("TraceId", "abcd123")
            header("ExecutionTime", "10")
        }
    }

    @Test
    fun `response with multi-value headers`() {
        wiremock.get {
            url equalTo "/hello"
        } returns {
            statusCode = 200
            header = "TraceId" to "abcd123"
            header = "TraceId" to "efgh456"
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(200)
            extract().headers().getValues("TraceId").also { values ->
                assertEquals(listOf("abcd123", "efgh456"), values)
            }
        }
    }

    fun ResponseSpecification.json(status: Int = 200) {
        withBuilder {
            withStatus(status)
            withHeader("Content-Type", "application/json")
        }
    }

    @Test
    fun `returns json`() {
        wiremock.get {
            url equalTo "/hello"
        } returns {
            json()
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

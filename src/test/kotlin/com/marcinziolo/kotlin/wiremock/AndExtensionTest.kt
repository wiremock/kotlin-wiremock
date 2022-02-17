package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@WireMockTest
class AndExtensionTest {
    @Test
    fun `request and`(wmRuntimeInfo: WireMockRuntimeInfo) {
        mockGet {
            url equalTo "/hello"
        } and {
            headers contains "User-Agent" equalTo "curl"
        } returns {
            statusCode = 200
        }

        Given {
            header("User-Agent", "curl")
        } When {
            get("${wmRuntimeInfo.httpBaseUrl}/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `response and`(wmRuntimeInfo: WireMockRuntimeInfo) {
        mockGet {
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
            get("${wmRuntimeInfo.httpBaseUrl}/hello")
        } Then {
            statusCode(200)
            body("pet", equalTo("dog"))
        }
    }
}

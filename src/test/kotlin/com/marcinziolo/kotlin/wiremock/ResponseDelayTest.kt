package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import io.mockk.mockk
import io.mockk.verify
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.junit.jupiter.api.Test

class ResponseDelayTest : AbstractTest() {
    @Test
    fun `delayed fixed response`() {
        wiremock.get {
            url equalTo "/hello"
        } returnsJson {
            body = """
                {"pet": "dog"}
            """
            delay fixedMs 100
        }
        for (x in (0..3)) {
            When {
                get("$url/hello")
            } Then {
                statusCode(200)
                body("pet", equalTo("dog"))
                time(greaterThanOrEqualTo(100L))
            }
        }
    }

    @Test
    fun `unit delayed median response`() {
        //given
        val wireMockServer = mockk<WireMockServer>(relaxed = true)
        val responseDefinitionBuilder = mockk<ResponseDefinitionBuilder>(relaxed = true)

        //when
        wireMockServer.get {
            url equalTo "/hello"
        }.copy(responseDefinitionBuilder = responseDefinitionBuilder) returns {
            delay medianMs 100 sigma 0.1
        }

        //then
        verify { responseDefinitionBuilder.withLogNormalRandomDelay(100.0, 0.1) }
    }
}

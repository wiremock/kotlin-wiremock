package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@WireMockTest
class Junit5ExtensionTest {

    lateinit var wm: WireMock
    lateinit var baseUrl: String

    @BeforeEach
    fun setup(wireMockRuntimeInfo: WireMockRuntimeInfo){
        wm = wireMockRuntimeInfo.wireMock
        baseUrl = wireMockRuntimeInfo.httpBaseUrl
    }

    @Test
    fun testGet() {
        wm.get {
            url equalTo "/hello"
        } returns {
            statusCode = 200
        }

        When {
            get("$baseUrl/hello")
        } Then {
            statusCode(200)
        }

        wm.verify {
            url equalTo "/hello"
        }
    }

    @Test
    fun testPost() {
        wm.post {
            url equalTo "/hello"
        } returns {
            statusCode = 200
        }

        When {
            post("$baseUrl/hello")
        } Then {
            statusCode(200)
        }
    }
}

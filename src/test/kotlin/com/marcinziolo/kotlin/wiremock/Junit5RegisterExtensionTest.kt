package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class Junit5RegisterExtensionTest {

    @JvmField
    @RegisterExtension
    var wm = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build()

    @Test
    fun testGet() {
        wm.get {
            url equalTo "/hello"
        } returns {
            statusCode = 200
        }

        When {
            get("${wm.baseUrl()}/hello")
        } Then {
            statusCode(200)
        }
    }
}

package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.VerificationException
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

class VerifyTest {

    @JvmField
    @RegisterExtension
    var wm = WireMockExtension.newInstance()
        .options(WireMockConfiguration.wireMockConfig().dynamicPort())
        .build()

    @Test
    fun `verify exactly`() {
        stubWiremock()
        performCall()

        wm.verify {
            url equalTo "/users/1"
            exactly = 1
        }

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                exactly = 2
            }
        }
    }

    @Test
    fun `verify atMost`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
            atMost = 2
        }

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                atMost = 1
            }
        }
    }

    @Test
    fun `verify atLeast`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
            atLeast = 2
        }

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                atLeast = 3
            }
        }
    }

    @Test
    fun `verify atLeast atMost`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
            atLeast = 1
            atMost = 3
        }

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                atLeast = 3
                atMost = 4
            }
        }
    }

    @Test
    fun `verify withBuilders`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            withBuilder { withUrl("/users/1") }
        }
    }

    @Test
    fun `verify method`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
            method = RequestMethod.GET
        }

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                method = RequestMethod.POST
            }
        }
    }

    @Test
    fun `verify urlLike`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url like "/users/.*"
        }
    }

    @Test
    fun `verify urlNotLike`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url notLike "/aaa"
        }
    }

    @Test
    fun `verify urlPath`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath equalTo "/users/1"
        }
    }

    @Test
    fun `verify urlPathLike`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath like "/users/[0-9]{0,1}"
        }
    }

    @Test
    fun `verify urlPathNotLike`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath notLike  "/users/[2-9]{0,1}"
        }
    }

    @Test
    fun `verify post requests`() {
        wm.post {
            url equalTo "/users/1?filter=true"
        } returnsJson { body = """{}""" }
        Given {
            header("User-Agent", "curl")
            queryParam("filter", true)
            cookie("cookieKey", "cookieValue")
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            post("${wm.baseUrl()}/users/1")
        } Then {
            statusCode(200)
        }

        wm.verify {
            headers contains "User-Agent" equalTo "curl"
            queryParams contains  "filter" equalTo "true"
            cookies contains  "cookieKey" equalTo "cookieValue"
            body contains "pet" equalTo "dog"
        }


        assertThrows<VerificationException> {
            wm.verify {
                headers contains "User-Agent" equalTo "curl2"
            }
        }

        assertThrows<VerificationException> {
            wm.verify {
                queryParams contains  "filter" equalTo "false"
            }
        }

        assertThrows<VerificationException> {
            wm.verify {
                cookies contains  "cookieKey" notLike "cookieValue"
            }
        }

        assertThrows<VerificationException> {
            wm.verify {
                body contains "pet" equalTo "cat"
            }
        }
    }

    private fun performCall(queryParams: String = "") {
        When {
            get("${wm.baseUrl()}/users/1" + queryParams)
        }
    }

    private fun stubWiremock() {
        wm.get {
            url equalTo "/users/1"
        } returns {
            header = "Content-Type" to "application/json"
            statusCode = 200
            body = """
                {
                  "id": 1,
                  "name": "Bob"
                }
                """
        }
    }
}

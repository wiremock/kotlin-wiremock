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
        } exactly 1

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } exactly 2
        }
    }

    @Test
    fun `verify lessEqual`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
        } lessThanOrExactly  2

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } lessThanOrExactly 1
        }
    }

    @Test
    fun `verify lessThan`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
        } lessThan 3

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } lessThan 2
        }
    }

    @Test
    fun `verify moreThan`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
        } moreThan  1

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } moreThan 2
        }
    }

    @Test
    fun `verify moreThanOrExactly`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
        } moreThanOrExactly 1

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } moreThanOrExactly 3
        }
    }

    @Test
    fun `verify between`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url equalTo "/users/1"
        } between  1 and 3

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
            } between 3 and 4
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
        } exactly 2

        assertThrows<VerificationException> {
            wm.verify {
                url equalTo "/users/1"
                method = RequestMethod.POST
            } exactly 2
        }
    }

    @Test
    fun `verify urlLike`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url like "/users/.*"
        } exactly 2
    }

    @Test
    fun `verify urlNotLike`() {
        stubWiremock()
        performCall()
        performCall()

        wm.verify {
            url notLike "/aaa"
        } exactly 2
    }

    @Test
    fun `verify urlPath`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath equalTo "/users/1"
        } exactly 1
    }

    @Test
    fun `verify urlPathLike`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath like "/users/[0-9]{0,1}"
        } exactly 1
    }

    @Test
    fun `verify urlPathNotLike`() {
        stubWiremock()
        performCall(queryParams = "?filter=true")

        wm.verify {
            urlPath notLike  "/users/[2-9]{0,1}"
        } exactly 1
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
        } exactly 1

        assertThrows<VerificationException> {
            wm.verify {
                headers contains "User-Agent" equalTo "curl2"
            } exactly 1
        }

        assertThrows<VerificationException> {
            wm.verify {
                queryParams contains  "filter" equalTo "false"
            } exactly 1
        }

        assertThrows<VerificationException> {
            wm.verify {
                cookies contains  "cookieKey" notLike "cookieValue"
            } exactly 1
        }

        assertThrows<VerificationException> {
            wm.verify {
                body contains "pet" equalTo "cat"
            } exactly 1
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

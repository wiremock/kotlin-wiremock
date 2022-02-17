package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.restassured.http.Method
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@WireMockTest
class RequestOtherMethodsExtensionTest {

    lateinit var url: String

    @BeforeEach
    fun urlSetup(wmRuntimeInfo: WireMockRuntimeInfo) {
        url = wmRuntimeInfo.httpBaseUrl
    }

    @Test
    fun `put method`() {
        mockPut {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            put("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `patch method`() {
        mockPatch {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            patch("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `options method`() {
        mockOptions {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            options("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `head method`() {
        mockHead {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            head("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `delete method`() {
        mockDelete {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            delete("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `trace method`() {
        mockTrace {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            request(Method.TRACE, "$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `delete and patch negative`() {
        mockDelete {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            patch("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `any method`() {
        mockAny {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            patch("$url/hello")
        } Then {
            statusCode(200)
        }
    }
}

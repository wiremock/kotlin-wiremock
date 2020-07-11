package com.marcinziolo.kotlin.wiremock

import io.restassured.http.Method
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test


class RequestOtherMethodsTest : AbstractTest() {
    @Test
    fun `put method`() {
        wiremock.put {
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
        wiremock.patch {
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
        wiremock.options {
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
        wiremock.head {
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
        wiremock.delete {
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
        wiremock.trace {
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
        wiremock.delete {
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
        wiremock.any {
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

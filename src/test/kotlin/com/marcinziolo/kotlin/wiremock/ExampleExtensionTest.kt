package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@WireMockTest
class ExampleExtensionTest {

    lateinit var url: String

    @BeforeEach
    fun urlSetup(wmRuntimeInfo: WireMockRuntimeInfo) {
        url = wmRuntimeInfo.httpBaseUrl
    }

    @Test
    fun `url equalTo`() {
        wireMockGet {
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

        When {
            get("$url/users/1")
        } Then {
            statusCode(200)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    @Test
    fun `returns json`() {
        wireMockGet {
            url like "/users/.*"
        } returnsJson {
            body = """
            {
              "id": 1,
              "name": "Bob"
            }
            """
        }

        When {
            get("$url/users/1")
        } Then {
            statusCode(200)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    val bobResponse: SpecifyResponse = {
        body = """
            {
              "id": 1,
              "name": "Bob"
            }
            """
    }

    val aliceResponse: SpecifyResponse = {
        body = """
            {
              "id": 2,
              "name": "Alice"
            }
            """
    }
    @Test

    fun `body checking`() {
        wireMockPost {
            url equalTo "/users"
            body contains "id" equalTo 1
            body contains "isAdmin" equalTo true
            body contains "points" equalTo 3.0
        } returnsJson bobResponse

        Given {
            body(//language=JSON
                """
                {
                 "id": 1,
                 "isAdmin": true,
                 "points": 3.0
                }
            """
            )
        } When {
            post("$url/users")
        } Then {
            statusCode(200)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    @Test
    fun `and`() {
        wireMockPost {
            url equalTo "/users"
        } and {
            body contains "id" equalTo 1
        } and {
            body contains "isAdmin" equalTo true
        } returns {
            header = "Content-Type" to "application/json"
        } and bobResponse and {
            statusCode = 201
        }

        Given {
            body(//language=JSON
                """
                {
                 "id": 1,
                 "isAdmin": true
                }
            """
            )
        } When {
            post("$url/users")
        } Then {
            statusCode(201)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    @Test
    fun `delay`() {
        wireMockPost {
            url equalTo "/users"
        } returnsJson {
            delay fixedMs 100
            delay medianMs 100 sigma 0.1
        } and bobResponse

        Given {
            body(//language=JSON
                """
                {
                 "id": 1,
                 "isAdmin": true
                }
            """
            )
        } When {
            post("$url/users")
        } Then {
            statusCode(200)
            body("id", equalTo(1))
            body("name", equalTo("Bob"))
        }
    }

    @Test
    fun `scenarios`() {
        wireMockPost {
            url equalTo "/users"
        } returnsJson bobResponse and {
            toState = "Alice"
        }

        wireMockPost {
            url equalTo "/users"
            whenState = "Alice"
        } returnsJson aliceResponse and {
            clearState = true
        }

        When {
            post("$url/users")
        } Then {
            body("name", equalTo("Bob"))
        }

        When {
            post("$url/users")
        } Then {
            body("name", equalTo("Alice"))
        }

        When {
            post("$url/users")
        } Then {
            body("name", equalTo("Bob"))
        }
    }
}

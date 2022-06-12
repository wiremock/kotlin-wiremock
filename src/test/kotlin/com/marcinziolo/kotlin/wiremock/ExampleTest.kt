package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ExampleTest : AbstractTest() {
    @Test
    fun `url equalTo`() {
        wiremock.get {
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
        wiremock.get {
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
        wiremock.post {
            url equalTo "/users"
            body contains "id" equalTo 1L
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
    fun `queryParams`() {
        wiremock.get {
            urlPath equalTo "/hello"
            queryParams contains "filter"
        } returns {
            statusCode = 200
        }
        When {
            get("$url/hello?filter=true")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `and`() {
        wiremock.post {
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
        wiremock.post {
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
        wiremock.post {
            url equalTo "/users"
        } returnsJson bobResponse and {
            toState = "Alice"
        }

        wiremock.post {
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

package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class RequestPostTest : AbstractTest() {
    @Test
    fun `post body negative`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet" equalTo "dog"
        } returnsJson { }

        When {
            post("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `post body positive`() {
        wiremock.post {
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
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `post body like positive`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet" like "d.*"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"doggy"}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `post body nested like positive`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet.type" like "d.*"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":{"type":"doggy"}}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `post body contains`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet.type" contains "ggy"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":{"type":"doggy"}}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `post body like negative`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet" like "dog.+"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `post body not like`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet" notLike "dog.+"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `post body contains just key`() {
        wiremock.post {
            url equalTo "/hello"
            body contains "pet"
        } returnsJson { }

        Given {
            body(//language=JSON
                """
                {"pet":"dog"}
            """
            )
        } When {
            post("$url/hello")
        } Then {
            statusCode(200)
        }
    }
}


package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test


internal class ScenarioTest : AbstractTest() {

    @Test
    fun `wiremock works in stateful stae`() {
        wiremock.post {
            url equalTo "/test"
            toState = "400"
        } returnsJson  {
            statusCode = 200
        }

        wiremock.post {
            url equalTo "/test"
            whenState = "400"
            toState = "500"

        } returnsJson  {
            statusCode = 400
        }

        wiremock.post {
            url equalTo "/test"
            whenState = "500"
            clearState = true

        } returnsJson  {
            statusCode = 500
        }

        When {
            post("$url/test")
        } Then {
            statusCode(200)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(400)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(500)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(200)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `wiremock stays in given sate`() {
        wiremock.post {
            url equalTo "/test"
            toState = "400"
        } returnsJson  {
            statusCode = 200
        }

        wiremock.post {
            url equalTo "/test"
            whenState = "400"
        } returnsJson  {
            statusCode = 400
        }


        When {
            post("$url/test")
        } Then {
            statusCode(200)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(400)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(400)
        }
    }

}
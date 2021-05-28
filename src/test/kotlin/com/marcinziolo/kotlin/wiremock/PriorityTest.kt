package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

internal class PriorityTest : AbstractTest() {

    @Test
    fun `prioritized response is returned`() {
        wiremock.post {
            url equalTo "/test"
            priority = 2
        } returnsJson  {
            statusCode = 403
        }

        wiremock.post {
            url equalTo "/test"
            headers contains "Authorization"
            priority = 1
        } returnsJson  {
            statusCode = 200
        }

        Given {
            header("Authorization", "Bearer token")
        } When {
            post("$url/test")
        } Then {
            statusCode(200)
        }

        When {
            post("$url/test")
        } Then {
            statusCode(403)
        }
    }

}

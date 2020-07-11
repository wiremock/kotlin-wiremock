package com.marcinziolo.kotlin.wiremock

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Test

class RequestQueryParamsTest : AbstractTest() {
    @Test
    fun `queryParams contains negative`() {
        wiremock.get {
            url equalTo "/hello"
            queryParams contains "any"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `queryParams contains positive`() {
        wiremock.get {
            url equalTo "/hello"
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
    fun `queryParams contains equalTo negative`() {
        wiremock.get {
            url equalTo "/hello"
            queryParams contains "filter" equalTo "true"
        } returns {
            statusCode = 200
        }

        When {
            get("$url/hello?filter=false")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `queryParams contains equalTo positive`() {
        wiremock.get {
            url equalTo "/hello"


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
    fun `queryParams contains like positive`() {
        wiremock.get {
            url equalTo "/hello"
            queryParams contains "filter" like "[rute]*"
        } returns {
            statusCode = 200
        }

        Given {
            queryParam("filter", "true")
        } When {
            get("$url/hello")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `queryParams contains like negative`() {
        wiremock.get {
            url equalTo "/hello"
            queryParams contains "filter" like "[tru]*"
        } returns {
            statusCode = 200
        }

        Given {
            queryParam("filter", "true")
        } When {
            get("$url/hello")
        } Then {
            statusCode(404)
        }
    }
}

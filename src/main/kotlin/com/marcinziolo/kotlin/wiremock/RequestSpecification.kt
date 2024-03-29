package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.notMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.matching.UrlPathPattern
import com.github.tomakehurst.wiremock.matching.UrlPattern
import com.github.tomakehurst.wiremock.stubbing.Scenario

class RequestSpecification {
    var whenState: String? = null
    var toState: String? = null
    var clearState: Boolean = false
    var priority = 1
    val urlPath = Whatever.wrap(StringConstraint::class)
    val url = Whatever.wrap(StringConstraint::class)
    val headers: MutableMap<String, StringConstraint> = mutableMapOf()
    val body: MutableMap<String, Constraint> = mutableMapOf()
    val cookies: MutableMap<String, StringConstraint> = mutableMapOf()
    val queryParams: MutableMap<String, StringConstraint> = mutableMapOf()
    private val builders: MutableList<MappingBuilder.() -> Unit> = mutableListOf()

    fun withBuilder(block: MappingBuilder.() -> Unit) {
        builders.add(block)
    }

    @SuppressWarnings("ComplexMethod")
    internal fun toMappingBuilder(method: Method): MappingBuilder =
        urlBuilder(method)
                    .also { headersBuilder(it) }
                    .also { bodyBuilder(it) }
                    .also { cookieBuilder(it) }
                    .also { queryParamsBuilder(it) }
                    .also { it.atPriority(priority) }
                    .also { scenarioBuilder(it) }
                    .also { this.builders.forEach { b -> it.b() } }


    private fun scenarioBuilder(it: MappingBuilder) {
        val newState = toState ?: if (clearState) Scenario.STARTED else whenState
        if (newState != null || toState != null || clearState) {
            it.inScenario("default")
                    .whenScenarioStateIs(whenState ?: Scenario.STARTED)
                    .willSetStateTo(newState)
        }
    }

    private fun urlBuilder(method: Method): MappingBuilder = when (val url = url.value) {
        is EqualTo -> method(urlEqualTo(url.value))
        is Like -> method(urlMatching(url.value))
        is NotLike -> method(UrlPattern(notMatching(url.value), true))
        is Whatever -> urlPathBuilder(method)
    }

    private fun urlPathBuilder(method: Method): MappingBuilder =
        when (val urlPath = urlPath.value) {
            is EqualTo -> method(urlPathEqualTo(urlPath.value))
            is Like -> method(urlPathMatching(urlPath.value))
            is NotLike -> method(UrlPathPattern(notMatching(urlPath.value), true))
            is Whatever -> method(urlPathMatching(".*"))
        }

    private fun headersBuilder(mappingBuilder: MappingBuilder) {
        headers.forEach {
            mappingBuilder.withHeader(
                    it.key,
                    getStringValuePattern(it.value)
            )
        }
    }

    private fun queryParamsBuilder(mappingBuilder: MappingBuilder) {
        queryParams.forEach {
            mappingBuilder.withQueryParam(
                    it.key,
                    getStringValuePattern(it.value)
            )
        }
    }

    private fun bodyBuilder(mappingBuilder: MappingBuilder) {
        body.toStringValuePatterns()
            .forEach { mappingBuilder.withRequestBody(it) }
    }

    private fun cookieBuilder(mappingBuilder: MappingBuilder) {
        cookies.forEach {
            mappingBuilder.withCookie(
                    it.key,
                    getStringValuePattern(it.value)
            )
        }
    }


    companion object {
        internal fun create(specifyRequestList: List<SpecifyRequest>): RequestSpecification {
            val requestSpecification = RequestSpecification()
            specifyRequestList.forEach { it(requestSpecification) }
            return requestSpecification
        }
    }
}

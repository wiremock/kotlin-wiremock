package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.CountMatchingStrategy
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.lessThanOrExactly
import com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.matching.UrlPathPattern
import com.github.tomakehurst.wiremock.matching.UrlPattern

class VerifySpecification {
    var method = RequestMethod.ANY
    val urlPath = Whatever.wrap(StringConstraint::class)
    val url = Whatever.wrap(StringConstraint::class)
    val headers: MutableMap<String, StringConstraint> = mutableMapOf()
    val body: MutableMap<String, Constraint> = mutableMapOf()
    val cookies: MutableMap<String, StringConstraint> = mutableMapOf()
    val queryParams: MutableMap<String, StringConstraint> = mutableMapOf()
    var exactly: Int? = null
    var atLeast: Int? = null
    var atMost: Int? = null
    private val builders: MutableList<RequestPatternBuilder.() -> Unit> = mutableListOf()

    fun withBuilder(block: RequestPatternBuilder.() -> Unit) {
        builders.add(block)
    }

    internal fun toCountMatchingStrategyList(): List<CountMatchingStrategy> {
        val countMatchingStrategyList = listOfNotNull(
            if (exactly != null) exactly(exactly!!) else null,
            if (atLeast != null) moreThanOrExactly(atLeast!!) else null,
            if (atMost != null) lessThanOrExactly(atMost!!) else null
        )
        return countMatchingStrategyList.ifEmpty { listOf(moreThanOrExactly(1)) }
    }

    internal fun toRequestPatternBuilder(): RequestPatternBuilder {
        val requestPatternBuilder = RequestPatternBuilder(method, urlBuilder())
        headers.forEach { requestPatternBuilder.withHeader(it.key, getStringValuePattern(it.value)) }
        cookies.forEach { requestPatternBuilder.withCookie(it.key, getStringValuePattern(it.value)) }
        queryParams.forEach { requestPatternBuilder.withQueryParam(it.key, getStringValuePattern(it.value)) }
        body.toStringValuePatterns().forEach { requestPatternBuilder.withRequestBody(it) }
        builders.forEach { requestPatternBuilder.it() }
        return requestPatternBuilder
    }

    private fun urlBuilder(): UrlPattern = when (val url = url.value) {
        is EqualTo -> WireMock.urlEqualTo(url.value)
        is Like -> WireMock.urlMatching(url.value)
        is NotLike -> UrlPattern(WireMock.notMatching(url.value), true)
        is Whatever -> urlPathBuilder()
    }

    private fun urlPathBuilder(): UrlPattern =
        when (val urlPath = urlPath.value) {
            is EqualTo -> WireMock.urlPathEqualTo(urlPath.value)
            is Like -> WireMock.urlPathMatching(urlPath.value)
            is NotLike -> UrlPathPattern(WireMock.notMatching(urlPath.value), true)
            is Whatever -> WireMock.urlPathMatching(".*")
        }
}

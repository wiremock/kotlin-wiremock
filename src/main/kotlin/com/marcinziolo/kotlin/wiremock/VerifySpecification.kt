package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
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
    private val builders: MutableList<RequestPatternBuilder.() -> Unit> = mutableListOf()

    fun withBuilder(block: RequestPatternBuilder.() -> Unit) {
        builders.add(block)
    }

    internal fun toRequestPatternBuilder(): RequestPatternBuilder {
        val requestPatternBuilder = RequestPatternBuilder(method, urlBuilder())
        headers.forEach { requestPatternBuilder.withHeader(it.key, getStringValuePattern(it.value))}
        cookies.forEach { requestPatternBuilder.withCookie(it.key, getStringValuePattern(it.value))}
        queryParams.forEach { requestPatternBuilder.withQueryParam(it.key, getStringValuePattern(it.value))}
        body.toStringValuePatterns().forEach { requestPatternBuilder.withRequestBody(it)}
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

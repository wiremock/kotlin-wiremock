package com.marcinziolo.kotlin.wiremock.mapper

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.matching
import com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath
import com.github.tomakehurst.wiremock.client.WireMock.notMatching
import com.github.tomakehurst.wiremock.matching.UrlPathPattern
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import com.marcinziolo.kotlin.wiremock.StringConstraint
import com.marcinziolo.kotlin.wiremock.EqualTo
import com.marcinziolo.kotlin.wiremock.EqualToJson
import com.marcinziolo.kotlin.wiremock.Like
import com.marcinziolo.kotlin.wiremock.Method
import com.marcinziolo.kotlin.wiremock.NotLike
import com.marcinziolo.kotlin.wiremock.RequestSpecification
import com.marcinziolo.kotlin.wiremock.StronglyEqualTo
import com.marcinziolo.kotlin.wiremock.Whatever

internal fun RequestSpecification.toMappingBuilder(method: Method): MappingBuilder =
    urlBuilder(method)
        .also { headersBuilder(it) }
        .also { bodyBuilder(it) }
        .also { cookieBuilder(it) }
        .also { queryParamsBuilder(it) }
        .also { it.atPriority(priority) }
        .also { scenarioBuilder(it) }

private fun RequestSpecification.scenarioBuilder(it: MappingBuilder) {
    val newState = toState ?: if (clearState) STARTED else whenState
    if (newState != null || toState != null || clearState) {
        it.inScenario("default")
            .whenScenarioStateIs(whenState ?: STARTED)
            .willSetStateTo(newState)
    }
}

private fun RequestSpecification.urlBuilder(method: Method): MappingBuilder =
    when (val url = url.value) {
        is EqualTo -> method(WireMock.urlPathEqualTo(url.value))
        is Like -> method(WireMock.urlPathMatching(url.value))
        is NotLike -> method(UrlPathPattern(notMatching(url.value), true))
        is Whatever -> method(WireMock.urlPathMatching(".*"))
    }

private fun RequestSpecification.headersBuilder(mappingBuilder: MappingBuilder) {
    headers.forEach {
        mappingBuilder.withHeader(
            it.key,
            getStringValuePattern(it.value)
        )
    }
}

private fun RequestSpecification.queryParamsBuilder(mappingBuilder: MappingBuilder) {
    queryParams.forEach {
        mappingBuilder.withQueryParam(
            it.key,
            getStringValuePattern(it.value)
        )
    }
}

private fun RequestSpecification.bodyBuilder(mappingBuilder: MappingBuilder) {
    body.forEach {
        val jsonRegex: String = when (val constraint = it.value) {
            is EqualTo -> "\$[?(@.${it.key} === '${constraint.value}')]"
            is StronglyEqualTo<*> -> "\$[?(@.${it.key} === ${constraint.value})]"
            is Like -> "\$[?(@.${it.key} =~ /${constraint.value}/i)]"
            is NotLike -> "\$[?(!(@.${it.key} =~ /${constraint.value}/i))]"
            is Whatever -> "\$.${it.key}"
            is EqualToJson -> constraint.value
        }
        if (it.value !is EqualToJson) {
            mappingBuilder.withRequestBody(matchingJsonPath(jsonRegex))
        } else {
            mappingBuilder.withRequestBody(equalToJson(jsonRegex))
        }
    }
}

private fun RequestSpecification.cookieBuilder(mappingBuilder: MappingBuilder) {
    cookies.forEach {
        mappingBuilder.withCookie(
            it.key,
            getStringValuePattern(it.value)
        )
    }
}

private fun getStringValuePattern(stringConstraint: StringConstraint) =
    when (stringConstraint) {
        is EqualTo -> equalTo(stringConstraint.value)
        is Like -> matching(stringConstraint.value)
        is NotLike -> notMatching(stringConstraint.value)
        is Whatever -> matching(".*")
    }

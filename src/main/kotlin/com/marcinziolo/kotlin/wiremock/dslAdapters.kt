package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.WireMock

internal fun Map<String, Constraint>.toStringValuePatterns() = this.entries.map {
    val jsonRegex: String = when (val constraint = it.value) {
        is EqualTo -> "\$[?(@.${it.key} === '${constraint.value}')]"
        is StronglyEqualTo<*> -> "\$[?(@.${it.key} === ${constraint.value})]"
        is Like -> "\$[?(@.${it.key} =~ /${constraint.value}/i)]"
        is NotLike -> "\$[?(!(@.${it.key} =~ /${constraint.value}/i))]"
        is Whatever -> "\$.${it.key}"
        is EqualToJson -> constraint.value
    }
    if (it.value !is EqualToJson) {
        WireMock.matchingJsonPath(jsonRegex)
    } else {
        WireMock.equalToJson(jsonRegex)
    }
}

internal fun getStringValuePattern(stringConstraint: StringConstraint) =
    when (stringConstraint) {
        is EqualTo -> WireMock.equalTo(stringConstraint.value)
        is Like -> WireMock.matching(stringConstraint.value)
        is NotLike -> WireMock.notMatching(stringConstraint.value)
        is Whatever -> WireMock.matching(".*")
    }

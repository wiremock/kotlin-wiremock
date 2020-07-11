package com.marcinziolo.kotlin.wiremock.mapper

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.marcinziolo.kotlin.wiremock.FixedDelay
import com.marcinziolo.kotlin.wiremock.NormalDistributionDelay
import com.marcinziolo.kotlin.wiremock.ResponseSpecification

fun ResponseSpecification.decorateResponseDefinitionBuilder(
    responseDefinitionBuilder: ResponseDefinitionBuilder
): ResponseDefinitionBuilder {

    responseDefinitionBuilder
        .withStatus(statusCode)
        .withBody(body.trimIndent())

    when (val delay = delay.value) {
        is FixedDelay -> responseDefinitionBuilder.withFixedDelay(delay.delay)
        is NormalDistributionDelay ->
            responseDefinitionBuilder.withLogNormalRandomDelay(delay.median.toDouble(), delay.sigma)
    }
    __headers.forEach { responseDefinitionBuilder.withHeader(it.key, it.value) }

    return responseDefinitionBuilder
}

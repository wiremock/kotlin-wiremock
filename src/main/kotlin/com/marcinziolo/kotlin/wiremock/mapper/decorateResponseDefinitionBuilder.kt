package com.marcinziolo.kotlin.wiremock.mapper

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.marcinziolo.kotlin.wiremock.FixedDelay
import com.marcinziolo.kotlin.wiremock.NormalDistributionDelay
import com.marcinziolo.kotlin.wiremock.ResponseSpecification

@SuppressWarnings("SpreadOperator")
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
    __headers.forEach { (key, values) -> responseDefinitionBuilder.withHeader(key, *values.toTypedArray()) }

    return responseDefinitionBuilder
}

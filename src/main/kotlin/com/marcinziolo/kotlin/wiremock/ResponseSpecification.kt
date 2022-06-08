package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder

class ResponseSpecification {
    var whenState: String? = null
    var toState: String? = null
    var clearState = false
    var body = ""
    var statusCode = 200
    @SuppressWarnings("VariableNaming")
    var __headers: MutableMap<String, MutableList<String>> = mutableMapOf()
    var header: Pair<String, String> = "" to ""
        set(value) {
            __headers.getOrPut(value.first) { mutableListOf() } += value.second
            field = value
        }
    var delay = FixedDelay(0)
        .wrap(Delay::class)

    private val builders: MutableList<ResponseDefinitionBuilder.() -> Unit> = mutableListOf()

    fun withBuilder(block: ResponseDefinitionBuilder.() -> Unit) {
        builders.add(block)
    }

    fun decorateResponseDefinitionBuilder(
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

        this.builders.forEach { b -> responseDefinitionBuilder.b() }

        return responseDefinitionBuilder
    }


    companion object {
        internal fun create(specifyResponseList: List<SpecifyResponse>): ResponseSpecification {
            val responseSpecification = ResponseSpecification()
            specifyResponseList.forEach { it(responseSpecification) }
            return responseSpecification
        }
    }
}

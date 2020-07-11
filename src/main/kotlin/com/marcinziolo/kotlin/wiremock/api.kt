package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.UrlPathPattern
import com.marcinziolo.kotlin.wiremock.mapper.decorateResponseDefinitionBuilder
import com.marcinziolo.kotlin.wiremock.mapper.toMappingBuilder
import java.util.UUID

typealias SpecifyRequest = RequestSpecification.() -> Unit
typealias SpecifyResponse = ResponseSpecification.() -> Unit
typealias Method = (UrlPathPattern) -> MappingBuilder

fun WireMockServer.get(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::get)
fun WireMockServer.post(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::post)
fun WireMockServer.put(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::put)
fun WireMockServer.patch(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::patch)
fun WireMockServer.delete(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::delete)
fun WireMockServer.head(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::head)
fun WireMockServer.options(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::options)
fun WireMockServer.trace(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::trace)
fun WireMockServer.any(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::any)

private fun WireMockServer.requestBuilderStep(
    specifyRequest: SpecifyRequest,
    method: Method
) = BuildingStep(
    wireMockServer = this,
    method = method,
    specifyRequestList = listOf(specifyRequest)
)

infix fun BuildingStep.and(specifyRequest: SpecifyRequest) =
    copy(specifyRequestList = specifyRequestList + specifyRequest)

infix fun BuildingStep.returnsJson(specifyResponse: SpecifyResponse) =
    this returns {
        statusCode = 200
        header = "Content-Type" to "application/json"
    } and specifyResponse

infix fun BuildingStep.returns(specifyResponse: SpecifyResponse) =
    copy(specifyResponseList = specifyResponseList + specifyResponse)
        .let {
            val returnsStep = ReturnsStep(it)
            returnsStep.buildingStep
                .assignId()
                .compute()
            returnsStep
        }

infix fun ReturnsStep.and(specifyResponse: SpecifyResponse) =
    copy(buildingStep = buildingStep.copy(specifyResponseList = buildingStep.specifyResponseList + specifyResponse))
        .let {
            it.buildingStep
                .assignId()
                .compute()
            it
        }

private fun BuildingStep.assignId(): BuildingStep {
    if (id != null) {
        wireMockServer.removeStubMapping(wireMockServer.getSingleStubMapping(id))
    }
    return this.copy(id = UUID.randomUUID())
}

private fun BuildingStep.compute(): BuildingStep {
    val requestSpecification = RequestSpecification
        .create(specifyRequestList)
    val responseSpecification = ResponseSpecification
        .create(specifyResponseList)

    requestSpecification copyScenariosAttributesFrom responseSpecification

    wireMockServer.stubFor(
        requestSpecification
            .toMappingBuilder(method)
            .withId(id)
            .willReturn(
                responseSpecification
                    .decorateResponseDefinitionBuilder(responseDefinitionBuilder)
            )
    )
    return this
}

private infix fun RequestSpecification.copyScenariosAttributesFrom(
    responseSpecification: ResponseSpecification
) {
    whenState = whenState ?: responseSpecification.whenState
    toState = toState ?: responseSpecification.toState
    clearState = clearState || responseSpecification.clearState
}


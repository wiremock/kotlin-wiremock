package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.marcinziolo.kotlin.wiremock.mapper.decorateResponseDefinitionBuilder
import com.marcinziolo.kotlin.wiremock.mapper.toMappingBuilder
import java.util.UUID

fun wireMockGet(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::get)
fun wireMockPost(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::post)
fun wireMockPut(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::put)
fun wireMockPatch(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::patch)
fun wireMockDelete(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::delete)
fun wireMockHead(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::head)
fun wireMockOptions(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::options)
fun wireMockTrace(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::trace)
fun wireMockAny(specifyRequest: SpecifyRequest) = requestBuilderStep(specifyRequest, WireMock::any)

private fun requestBuilderStep(
    specifyRequest: SpecifyRequest,
    method: Method
) = DefaultBuildingStep(
    method = method,
    specifyRequestList = listOf(specifyRequest)
)

infix fun DefaultBuildingStep.and(specifyRequest: SpecifyRequest) =
    copy(specifyRequestList = specifyRequestList + specifyRequest)

infix fun DefaultBuildingStep.returnsJson(specifyResponse: SpecifyResponse) =
    this returns {
        statusCode = 200
        header = "Content-Type" to "application/json"
    } and specifyResponse

infix fun DefaultBuildingStep.returns(specifyResponse: SpecifyResponse) =
    copy(specifyResponseList = specifyResponseList + specifyResponse)
        .let {
            val returnsStep = DefaultReturnsStep(it)
            returnsStep.buildingStep
                .assignId()
                .compute()
            returnsStep
        }

infix fun DefaultReturnsStep.and(specifyResponse: SpecifyResponse) =
    copy(buildingStep = buildingStep.copy(specifyResponseList = buildingStep.specifyResponseList + specifyResponse))
        .let {
            it.buildingStep
                .assignId()
                .compute()
            it
        }

private fun DefaultBuildingStep.assignId(): DefaultBuildingStep {
    if (id != null) {
        WireMock.removeStub(WireMock.getSingleStubMapping(id))
    }
    return this.copy(id = UUID.randomUUID())
}

private fun DefaultBuildingStep.compute(): DefaultBuildingStep {
    val requestSpecification = RequestSpecification
        .create(specifyRequestList)
    val responseSpecification = ResponseSpecification
        .create(specifyResponseList)

    requestSpecification copyScenariosAttributesFrom responseSpecification

    WireMock.stubFor(
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


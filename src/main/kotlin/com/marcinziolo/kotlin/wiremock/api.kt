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

fun WireMock.get(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::get)
fun WireMock.post(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::post)
fun WireMock.put(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::put)
fun WireMock.patch(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::patch)
fun WireMock.delete(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::delete)
fun WireMock.head(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::head)
fun WireMock.options(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::options)
fun WireMock.trace(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::trace)
fun WireMock.any(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::any)
fun WireMockServer.get(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::get)
fun WireMockServer.post(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::post)
fun WireMockServer.put(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::put)
fun WireMockServer.patch(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::patch)
fun WireMockServer.delete(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::delete)
fun WireMockServer.head(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::head)
fun WireMockServer.options(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::options)
fun WireMockServer.trace(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::trace)
fun WireMockServer.any(specifyRequest: SpecifyRequest) = requestServerBuilderStep(specifyRequest, WireMock::any)
fun mockGet(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::get)
fun mockPost(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::post)
fun mockPut(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::put)
fun mockPatch(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::patch)
fun mockDelete(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::delete)
fun mockHead(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::head)
fun mockOptions(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::options)
fun mockTrace(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::trace)
fun mockAny(specifyRequest: SpecifyRequest) = requestDefaultBuilderStep(specifyRequest, WireMock::any)

private fun WireMock.requestServerBuilderStep(
        specifyRequest: SpecifyRequest,
        method: Method
) = BuildingStep(
        wireMockInstance = WiremockClientInstance(this),
        method = method,
        specifyRequestList = listOf(specifyRequest)
)

fun WireMockServer.requestServerBuilderStep(
    specifyRequest: SpecifyRequest,
    method: Method
) = BuildingStep(
    wireMockInstance = WiremockServerInstance(this),
    method = method,
    specifyRequestList = listOf(specifyRequest)
)

private fun requestDefaultBuilderStep(
    specifyRequest: SpecifyRequest,
    method: Method
) = BuildingStep(
    wireMockInstance = WiremockDefaultInstance,
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
        wireMockInstance.removeStubMapping(wireMockInstance.getSingleStubMapping(id))
    }
    return this.copy(id = UUID.randomUUID())
}

private fun BuildingStep.compute(): BuildingStep {
    val requestSpecification = RequestSpecification
        .create(specifyRequestList)
    val responseSpecification = ResponseSpecification
        .create(specifyResponseList)

    requestSpecification copyScenariosAttributesFrom responseSpecification

    wireMockInstance.stubFor(
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

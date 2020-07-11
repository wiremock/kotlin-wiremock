package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import java.util.UUID

data class BuildingStep(
    val id: UUID? = null,
    val wireMockServer: WireMockServer,
    val method: Method,
    val specifyRequestList: List<SpecifyRequest>,
    val specifyResponseList: List<SpecifyResponse> = emptyList(),
    val responseDefinitionBuilder: ResponseDefinitionBuilder = WireMock.aResponse()
)

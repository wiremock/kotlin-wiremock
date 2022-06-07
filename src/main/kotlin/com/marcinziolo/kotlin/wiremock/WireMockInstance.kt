package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import java.util.UUID

sealed class WireMockInstance {
    abstract fun stubFor(mappingBuilder: MappingBuilder): StubMapping
    abstract fun removeStubMapping(stubMapping: StubMapping)
    abstract fun getSingleStubMapping(uuid: UUID): StubMapping
}

object WiremockDefaultInstance: WireMockInstance() {
    override fun stubFor(mappingBuilder: MappingBuilder) = WireMock.stubFor(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = WireMock.removeStub(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = WireMock.getSingleStubMapping(uuid)
}

class WiremockServerInstance(private val wireMockServer: WireMockServer): WireMockInstance() {
    override fun stubFor(mappingBuilder: MappingBuilder) = wireMockServer.stubFor(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = wireMockServer.removeStub(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = wireMockServer.getSingleStubMapping(uuid)
}

class WiremockClientInstance(private val wireMock: WireMock): WireMockInstance() {
    override fun stubFor(mappingBuilder: MappingBuilder) = wireMock.register(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = wireMock.removeStubMapping(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = wireMock.getStubMapping(uuid).item
}

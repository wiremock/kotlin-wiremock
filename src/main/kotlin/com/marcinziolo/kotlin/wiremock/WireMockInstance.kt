package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.DslWrapper
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import java.util.UUID

interface WireMockInstance {
    fun stubFor(mappingBuilder: MappingBuilder): StubMapping
    fun removeStubMapping(stubMapping: StubMapping)
    fun getSingleStubMapping(uuid: UUID): StubMapping
}

class WiremockDefaultInstance: WireMockInstance {
    override fun stubFor(mappingBuilder: MappingBuilder) = WireMock.stubFor(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = WireMock.removeStub(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = WireMock.getSingleStubMapping(uuid)
}

class WiremockServerInstance(private val wireMockServer: WireMockServer): WireMockInstance {
    override fun stubFor(mappingBuilder: MappingBuilder) = wireMockServer.stubFor(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = wireMockServer.removeStub(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = wireMockServer.getSingleStubMapping(uuid)
}

class WiremockClientInstance(private val wireMock: WireMock): WireMockInstance {
    override fun stubFor(mappingBuilder: MappingBuilder) = wireMock.register(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = wireMock.removeStubMapping(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = wireMock.getStubMapping(uuid).item
}

class WiremockDslWrapperInstance(private val dslWrapper: DslWrapper): WireMockInstance {
    override fun stubFor(mappingBuilder: MappingBuilder) = dslWrapper.stubFor(mappingBuilder)
    override fun removeStubMapping(stubMapping: StubMapping) = dslWrapper.removeStub(stubMapping)
    override fun getSingleStubMapping(uuid: UUID) = dslWrapper.getSingleStubMapping(uuid)
}

package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class AbstractTest {
    private val port = findRandomOpenPort()
    val wiremock: WireMockServer
    val url
        get() = "http://localhost:$port"

    init {
        wiremock = WireMockServer(options().port(port).notifier(ConsoleNotifier(true)))
    }

    @BeforeEach
    fun setUp() {
        wiremock.start()
    }

    @AfterEach
    fun afterEach() {
        wiremock.resetAll()
        wiremock.stop()
    }
}
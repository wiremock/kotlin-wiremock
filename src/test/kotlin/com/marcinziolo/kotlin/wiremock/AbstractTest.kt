package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@Suppress("UnnecessaryAbstractClass")
abstract class AbstractTest {
    private val port = findRandomOpenPort()
    val wiremock: WireMockServer = WireMockServer(options().port(port).notifier(ConsoleNotifier(true)))
    val url
        get() = "http://localhost:$port"

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

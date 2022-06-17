package com.marcinziolo.kotlin.wiremock

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.client.WireMock.exactly as wiremockExactly
import com.github.tomakehurst.wiremock.client.WireMock.lessThan as wiremockLessThan
import com.github.tomakehurst.wiremock.client.WireMock.lessThanOrExactly as wiremockLessThanOrExactly
import com.github.tomakehurst.wiremock.client.WireMock.moreThan as wiremockMoreThan
import com.github.tomakehurst.wiremock.client.WireMock.moreThanOrExactly as wiremockMoreThanOrExactly

class CountStep(
    private val wiremockInstance: WireMockInstance,
    private val requestPatternBuilder: RequestPatternBuilder
) {
    infix fun exactly(count: Int) {
        wiremockInstance.verify(wiremockExactly(count), requestPatternBuilder)
    }

    infix fun lessThan(count: Int) {
        wiremockInstance.verify(wiremockLessThan(count), requestPatternBuilder)
    }

    infix fun lessThanOrExactly(count: Int) {
        wiremockInstance.verify(wiremockLessThanOrExactly(count), requestPatternBuilder)
    }

    infix fun moreThanOrExactly(count: Int) {
        wiremockInstance.verify(wiremockMoreThanOrExactly(count), requestPatternBuilder)
    }

    infix fun moreThan(count: Int) {
        wiremockInstance.verify(wiremockMoreThan(count), requestPatternBuilder)
    }

    infix fun between(from: Int): CountBetweenStep = CountBetweenStep(this, from)

    class CountBetweenStep(private val countStep: CountStep, private val from: Int) {
        infix fun and(to: Int) {
            countStep.wiremockInstance.verify(wiremockMoreThanOrExactly(from), countStep.requestPatternBuilder)
            countStep.wiremockInstance.verify(wiremockLessThanOrExactly(to), countStep.requestPatternBuilder)
        }
    }
}

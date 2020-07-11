package com.marcinziolo.kotlin.wiremock

sealed class Delay

data class FixedDelay(val delay: Int) : Delay()
data class NormalDistributionDelay(val median: Int, val sigma: Double) : Delay()

infix fun Wrapper<in Delay>.fixedMs(delay: Int) {
    this.value = FixedDelay(delay)
}

infix fun Wrapper<in Delay>.medianMs(median: Int): MedianStep {
    val normalDistribution =
        NormalDistributionDelay(median, 0.0)
    this.value = normalDistribution
    return MedianStep(this as Wrapper<NormalDistributionDelay>)
}

data class MedianStep(val delay: Wrapper<NormalDistributionDelay>)

infix fun MedianStep.sigma(sigma: Double) {
    this.delay.value = this.delay.value.copy(sigma = sigma)
}

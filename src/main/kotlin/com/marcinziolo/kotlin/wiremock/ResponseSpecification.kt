package com.marcinziolo.kotlin.wiremock

class ResponseSpecification {
    var whenState: String? = null
    var toState: String? = null
    var clearState = false
    var body = ""
    var statusCode = 200
    @SuppressWarnings("VariableNaming")
    var __headers: MutableMap<String, MutableList<String>> = mutableMapOf()
    var header: Pair<String, String> = "" to ""
        set(value) {
            __headers.getOrPut(value.first) { mutableListOf() } += value.second
            field = value
        }
    var delay = FixedDelay(0)
        .wrap(Delay::class)

    companion object {
        internal fun create(specifyResponseList: List<SpecifyResponse>): ResponseSpecification {
            val responseSpecification = ResponseSpecification()
            specifyResponseList.forEach { it(responseSpecification) }
            return responseSpecification
        }
    }
}

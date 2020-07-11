package com.marcinziolo.kotlin.wiremock

class RequestSpecification {
    var whenState: String? = null
    var toState: String? = null
    var clearState: Boolean = false
    var priority = 1
    val url = EqualTo("").wrap(StringConstraint::class)
    val headers: MutableMap<String, StringConstraint> = mutableMapOf()
    val body: MutableMap<String, Constraint> = mutableMapOf()
    val cookies: MutableMap<String, StringConstraint> = mutableMapOf()
    val queryParams: MutableMap<String, StringConstraint> = mutableMapOf()

    companion object {
        internal fun create(specifyRequestList: List<SpecifyRequest>): RequestSpecification {
            val requestSpecification = RequestSpecification()
            specifyRequestList.forEach { it(requestSpecification) }
            return requestSpecification
        }
    }
}

package com.marcinziolo.kotlin.wiremock

import java.util.*

sealed class ContainsStep<T>(open val key: String, open val map: MutableMap<String, T>)

data class ConstraintContainsStep(
    override val key: String,
    override val map: MutableMap<String, Constraint>
) : ContainsStep<Constraint>(key, map)

data class StringContainsStep(
    override val key: String,
    override val map: MutableMap<String, StringConstraint>
) : ContainsStep<StringConstraint>(key, map)

infix fun MutableMap<String, StringConstraint>.contains(key: String): StringContainsStep {
    this[key] = Whatever
    return StringContainsStep(key, this)
}

infix fun MutableMap<String, Constraint>.equalTo(json: String): ConstraintContainsStep {
    val key = UUID.randomUUID().toString()
    this[key] = EqualToJson(json)
    return ConstraintContainsStep(key, this)
}

infix fun MutableMap<String, Constraint>.contains(key: String): ConstraintContainsStep {
    this[key] = Whatever
    return ConstraintContainsStep(key, this)
}

@Suppress("UNCHECKED_CAST")
infix fun <T> ContainsStep<T>.equalTo(value: String) {
    this.map[this.key] = EqualTo(value) as T
}

infix fun ContainsStep<Constraint>.equalTo(value: Int) {
    this.map[this.key] = StronglyEqualTo(value)
}

infix fun ContainsStep<Constraint>.equalTo(value: Boolean) {
    this.map[this.key] = StronglyEqualTo(value)
}

infix fun ContainsStep<Constraint>.equalTo(value: Double) {
    this.map[this.key] = StronglyEqualTo(value)
}

@Suppress("UNCHECKED_CAST")
infix fun <T> ContainsStep<T>.like(value: String) {
    this.map[this.key] = Like(value) as T
}

@Suppress("UNCHECKED_CAST")
infix fun <T> ContainsStep<T>.contains(value: String) {
    this.map[this.key] = Like(".*$value.*") as T
}

@Suppress("UNCHECKED_CAST")
infix fun <T> ContainsStep<T>.notLike(value: String) {
    this.map[this.key] = NotLike(value) as T
}

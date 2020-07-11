package com.marcinziolo.kotlin.wiremock

sealed class Constraint
sealed class StringConstraint: Constraint()
data class EqualTo(val value: String) : StringConstraint()
data class Like(val value: String) : StringConstraint()
data class NotLike(val value: String) : StringConstraint()
object Whatever : StringConstraint()
data class StronglyEqualTo<T>(val value: T): Constraint()

infix fun Wrapper<in StringConstraint>.equalTo(value: String) {
    this.value = EqualTo(value)
}

infix fun Wrapper<in StringConstraint>.like(value: String) {
    this.value = Like(value)
}

infix fun Wrapper<in StringConstraint>.notLike(value: String) {
    this.value = NotLike(value)
}

infix fun Wrapper<in StringConstraint>.contains(value: String) {
    this.value = Like(".*$value.*")
}

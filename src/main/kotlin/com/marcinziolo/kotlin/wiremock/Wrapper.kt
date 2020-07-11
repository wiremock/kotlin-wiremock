package com.marcinziolo.kotlin.wiremock

import kotlin.reflect.KClass

@SuppressWarnings("UseDataClass")
class Wrapper<T>(var value: T)

@SuppressWarnings("UnusedPrivateMember")
fun <T : Any> Any.wrap(claz: KClass<T>) = Wrapper(this as T)

package com.marcinziolo.kotlin.wiremock

import kotlin.reflect.KClass

@Suppress("UseDataClass")
class Wrapper<T>(var value: T)

@Suppress("UNUSED_PARAMETER","UNCHECKED_CAST")
fun <T : Any> Any.wrap(claz: KClass<T>) = Wrapper(this as T)

package rj.kilikili.utils.extensions

fun <T> MutableCollection<T>.addReturning(data: T): T = data.also { add(it) }
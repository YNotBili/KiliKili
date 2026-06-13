package rj.kilikili.utils.extensions

import rj.kilikili.api.BilibiliApiException

fun Throwable.msg() =
    if (this is BilibiliApiException && cause == null) {
        "$code $message"
    } else {
        toString()
    }
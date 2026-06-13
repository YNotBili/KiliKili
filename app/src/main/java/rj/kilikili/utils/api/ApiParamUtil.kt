package rj.kilikili.utils.api

import java.util.Random

val uniqId
    get() = (Random().nextDouble() * (1500000000000L - 1300000000000L)).toLong()
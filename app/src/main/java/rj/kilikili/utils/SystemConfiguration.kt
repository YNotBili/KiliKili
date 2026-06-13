package rj.kilikili.utils

import android.content.res.Resources
import android.os.Build

val isRound = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Resources.getSystem().configuration.isScreenRound
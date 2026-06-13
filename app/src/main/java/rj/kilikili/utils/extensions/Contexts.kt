package rj.kilikili.utils.extensions

import android.content.Context
import rj.kilikili.ui.activity.base.BaseActivity

val Context.originalConfigContext
    get() = (this as? BaseActivity)?.originalViewContext ?: this
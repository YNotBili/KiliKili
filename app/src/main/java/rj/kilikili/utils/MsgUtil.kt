package rj.kilikili.utils

import rj.kilikili.ui.common.SnackbarDuration
import rj.kilikili.ui.common.SnackbarManager

object MsgUtil {

    @JvmStatic
    fun showMsg(str: String) {
        SnackbarManager.show(str, SnackbarDuration.Short)
    }

    @JvmStatic
    fun showMsgLong(str: String) {
        SnackbarManager.show(str, SnackbarDuration.Long)
    }

}
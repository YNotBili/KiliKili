package rj.kilikili.data.account

import rj.kilikili.KiliKili
import rj.kilikili.R
import rj.kilikili.applicationContext
import rj.kilikili.data.di.AppDependenciesEntryPoint
import rj.kilikili.data.account.AccountManager.currentAccount
import rj.kilikili.utils.MsgUtil
import rj.kilikili.utils.runOnUi
import dagger.hilt.android.EntryPointAccessors

object AccountManager {

    val repository by lazy {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            KiliKili.application,
            AppDependenciesEntryPoint::class.java
        )
        hiltEntryPoint.accountRepository()
    }

    val currentAccount: AccountEntity
        get() = repository.activeAccount.value ?: emptyAccount

    fun loggedIn() = currentAccount.accountId != 0L

}

inline fun requireLoggedIn(
    displayMsg: Boolean = true,
    block: (AccountEntity) -> Unit
) {
    if (!AccountManager.loggedIn() && displayMsg) {
        runOnUi {
            MsgUtil.showMsg(applicationContext.getString(R.string.not_logged_in))
        }
    } else {
        block(currentAccount)
    }
}

inline fun runIfNotLoggedIn(
    block: (() -> Unit) = { }
) {
    if (!AccountManager.loggedIn()) {
        block()
    }
}

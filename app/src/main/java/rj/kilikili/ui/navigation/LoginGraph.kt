package rj.kilikili.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import rj.kilikili.ui.navigation.appComposable
import rj.kilikili.ui.screens.login.HdQrCodeLoginHost

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    appComposable(NavGraph.LOGIN) {
        HdQrCodeLoginHost(
            onLoginSuccess = onLoginSuccess,
            onSkip = onSkip
        )
    }
}
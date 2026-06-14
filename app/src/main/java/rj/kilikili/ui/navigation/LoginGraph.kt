package rj.kilikili.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.wear.compose.navigation.composable
import rj.kilikili.ui.screens.login.HdQrCodeLoginHost

// @Removal: 旧登录入口 LoginScreenHost，已被 HdQrCodeLoginHost 替代

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    composable(NavGraph.LOGIN) {
        HdQrCodeLoginHost(
            onLoginSuccess = onLoginSuccess,
            onSkip = onSkip
        )
    }
}
package rj.kilikili.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.wear.compose.navigation.composable
import rj.kilikili.ui.screens.login.LoginScreenHost

fun NavGraphBuilder.loginGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    composable(NavGraph.LOGIN) {
        LoginScreenHost(onLoginSuccess = onLoginSuccess, onSkip = onSkip)
    }
}
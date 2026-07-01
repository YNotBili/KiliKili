package rj.kilikili.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import rj.kilikili.ui.common.CustomSnackbarHost
import rj.kilikili.ui.screens.main.MainScreen
import rj.kilikili.ui.screens.main.MainUiState
import rj.kilikili.ui.screens.main.MainViewModel
import rj.kilikili.ui.screens.setup.SetupScreen

/** 顶层 NavHost 入口 — 集成 MainViewModel 决定 startDestination, 然后调底层 AppNavHost。 */
@Composable
fun AppNavHost(mainViewModel: MainViewModel = hiltViewModel()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val navController = rememberAppNavController()

    val startDestination = when (uiState) {
        is MainUiState.NeedsSetup -> NavGraph.SETUP
        is MainUiState.NeedsLogin -> NavGraph.LOGIN
        is MainUiState.Ready -> NavGraph.MAIN
        else -> NavGraph.MAIN
    }

    Box {
        AppNavHostRoute(
            navController = navController,
            startDestination = startDestination
        ) { nc ->
            appComposable(NavGraph.SETUP) {
                SetupScreen(
                    onSetupComplete = {
                        mainViewModel.onSetupComplete()
                        nc.navigate(NavGraph.LOGIN) {
                            popUpTo(NavGraph.SETUP) { inclusive = true }
                        }
                    }
                )
            }

            loginGraph(
                navController = nc,
                onLoginSuccess = {
                    nc.navigate(NavGraph.MAIN) {
                        popUpTo(NavGraph.LOGIN) { inclusive = true }
                    }
                },
                onSkip = {
                    nc.navigate(NavGraph.MAIN) {
                        popUpTo(NavGraph.LOGIN) { inclusive = true }
                    }
                }
            )

            mainGraph(nc)
        }
        CustomSnackbarHost()
    }
}

fun NavGraphBuilder.mainGraph(navController: NavController) {
    appComposable(NavGraph.MAIN) {
        MainScreen(navController)
    }
}
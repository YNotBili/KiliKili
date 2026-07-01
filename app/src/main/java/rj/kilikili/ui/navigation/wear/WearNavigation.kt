package rj.kilikili.ui.navigation.wear

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController

/** Wear 端 NavHost — SwipeDismissableNavHost 带左滑返回手势。 */
@Composable
fun WearNavHost(
    navController: NavHostController = rememberSwipeDismissableNavController(),
    startDestination: String,
    route: String? = null,
    builder: NavGraphBuilder.(NavHostController) -> Unit
) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = startDestination,
        route = route
    ) {
        builder(navController)
    }
}

fun NavGraphBuilder.wearComposable(
    route: String,
    arguments: List<androidx.navigation.NamedNavArgument> = emptyList(),
    deepLinks: List<androidx.navigation.NavDeepLink> = emptyList(),
    content: @Composable (androidx.navigation.NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks
    ) { entry -> content(entry) }
}
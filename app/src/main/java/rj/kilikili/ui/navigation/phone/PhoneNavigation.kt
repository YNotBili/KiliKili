package rj.kilikili.ui.navigation.phone

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/** Phone 端 NavHost — 标准 androidx.navigation.compose NavHost，无左滑返回手势。 */
@Composable
fun PhoneNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    route: String? = null,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.(NavHostController) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        route = route,
        modifier = modifier
    ) {
        builder(navController)
    }
}

fun NavGraphBuilder.phoneComposable(
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
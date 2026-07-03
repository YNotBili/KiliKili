package rj.kilikili.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import rj.kilikili.UiType
import rj.kilikili.uiType
import rj.kilikili.ui.navigation.phone.PhoneNavHost
import rj.kilikili.ui.navigation.phone.phoneComposable
import rj.kilikili.ui.navigation.wear.WearNavHost
import rj.kilikili.ui.navigation.wear.wearComposable

/** 统一 remember — 返回 [NavHostController]。 */
@Composable
fun rememberAppNavController(): NavHostController = when (uiType) {
    UiType.WEAR, UiType.FRESHWEAR -> androidx.wear.compose.navigation.rememberSwipeDismissableNavController()
    UiType.PHONE -> androidx.navigation.compose.rememberNavController()
}

/**
 * 底层 NavHost 入口 — wear SwipeDismissableNavHost (带左滑返回), phone 标准 NavHost (无左滑, 让位给 Drawer)。
 * screens / graphs 应该调顶层 [AppNavHost] 而不是这个。
 */
@Composable
fun AppNavHostRoute(
    navController: NavHostController = rememberAppNavController(),
    startDestination: String,
    route: String? = null,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.(NavHostController) -> Unit
) {
    when (uiType) {
        UiType.WEAR, UiType.FRESHWEAR -> WearNavHost(
            navController = navController,
            startDestination = startDestination,
            route = route,
            builder = builder
        )
        UiType.PHONE -> PhoneNavHost(
            navController = navController,
            startDestination = startDestination,
            route = route,
            modifier = modifier,
            builder = builder
        )
    }
}

/** 统一 composable extension — screens / graphs 用这个替代直接的 wear/phone composable。 */
fun NavGraphBuilder.appComposable(
    route: String,
    arguments: List<androidx.navigation.NamedNavArgument> = emptyList(),
    deepLinks: List<androidx.navigation.NavDeepLink> = emptyList(),
    content: @Composable (androidx.navigation.NavBackStackEntry) -> Unit
) {
    when (uiType) {
        UiType.WEAR, UiType.FRESHWEAR -> wearComposable(
            route = route,
            arguments = arguments,
            deepLinks = deepLinks,
            content = content
        )
        UiType.PHONE -> phoneComposable(
            route = route,
            arguments = arguments,
            deepLinks = deepLinks,
            content = content
        )
    }
}
package rj.kilikili.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import rj.kilikili.ui.activity.base.BaseActivity
import rj.kilikili.ui.navigation.AppNavHost
import rj.kilikili.ui.theme.BiliZepamTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiliZepamTheme {
                AppNavHost()
            }
        }
    }

}

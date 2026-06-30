package rj.kilikili.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.key
import rj.kilikili.ui.activity.base.BaseActivity
import rj.kilikili.ui.navigation.AppNavHost
import rj.kilikili.ui.theme.BiliZepamTheme
import rj.kilikili.uiType
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiliZepamTheme {
                key(uiType) {
                    AppNavHost()
                }
            }
        }
    }

}

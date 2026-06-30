package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.PaddingDefaults
import rj.kilikili.UiType

@Composable
fun appVerticalOptContentPadding(): Dp = when (actualUiType) {
    UiType.WEAR -> PaddingDefaults.verticalOptContentPadding()
    UiType.PHONE -> 8.dp
}

@Composable
fun appVerticalContentPadding(): PaddingValues = when (actualUiType) {
    UiType.WEAR -> {
        val dp = PaddingDefaults.verticalContentPadding()
        PaddingValues(horizontal = 0.dp, vertical = dp)
    }
    UiType.PHONE -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
}

@Composable
fun appHorizontalContentPadding(enabled: Boolean = true): PaddingValues = when (actualUiType) {
    UiType.WEAR -> {
        val dp = PaddingDefaults.horizontalContentPadding()
        PaddingValues(horizontal = if (enabled) dp else 0.dp, vertical = 0.dp)
    }
    UiType.PHONE -> PaddingValues(horizontal = if (enabled) 16.dp else 0.dp, vertical = 0.dp)
}

@Composable
fun appListTopSpacer() = Spacer(Modifier.height(if (actualUiType == UiType.WEAR) 24.dp else 16.dp))

@Composable
fun appListBottomSpacer() = Spacer(Modifier.height(if (actualUiType == UiType.WEAR) 20.dp else 16.dp))

@Composable
fun appListHeader(title: String, modifier: Modifier = Modifier) {
    when (actualUiType) {
        UiType.WEAR -> {
            ListHeader(modifier = modifier.fillMaxWidth()) {
                Text(text = title)
            }
        }
        UiType.PHONE -> {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

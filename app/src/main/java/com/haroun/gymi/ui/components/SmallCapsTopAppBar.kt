// app/src/main/java/com/haroun/gymi/ui/components/SmallCapsTopAppBar.kt
package com.haroun.gymi.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallCapsTopAppBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            // Try fontFeatureSettings for small-caps; fallback to uppercase + bold
            val style = MaterialTheme.typography.titleLarge
            Text(
                text = title.uppercase(), // uppercase fallback
                style = style.copy(fontWeight = FontWeight.Bold)
            )
        }
    )
}

package tn.ibtidai.tania.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Correct = Color(0xFF2E7D32)
val CorrectBg = Color(0xFFE8F5E9)
val Wrong = Color(0xFFC62828)
val WrongBg = Color(0xFFFFEBEE)
val Model = Color(0xFF1565C0)

@Composable
fun TaniaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF3F51B5),
            background = Color(0xFFFFFBF2),
            surface = Color(0xFFFFFBF2),
        ),
        content = content,
    )
}

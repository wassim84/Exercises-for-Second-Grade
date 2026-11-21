package tn.ibtidai.tania.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.ibtidai.tania.R

val SplashBlue = Color(0xFF29B6F6)

/** حِكم تظهر واحدة منها عشوائيًا عند كل فتح للتطبيق */
private val SAYINGS = listOf(
    "بالعلم نتميّز ✨",
    "العلم نور 💡",
    "من جدّ وجد، ومن زرع حصد 🌱",
    "﴿ وَقُل رَّبِّ زِدْنِي عِلْمًا ﴾",
    "القراءة مفتاح المعرفة 📖",
)

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val saying = remember { SAYINGS.random() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBlue)
            .clickable { onDone() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.size(200.dp),
        )
        Spacer(Modifier.height(24.dp))
        Text("السنة الثانية ابتدائي", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Text(
            saying,
            color = Color(0xFFFFEB3B),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 42.sp,
        )
    }
}

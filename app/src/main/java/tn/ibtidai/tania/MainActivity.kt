package tn.ibtidai.tania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import tn.ibtidai.tania.data.Exercise
import tn.ibtidai.tania.data.ExerciseRepository
import tn.ibtidai.tania.data.Subject
import tn.ibtidai.tania.ui.ExerciseListScreen
import tn.ibtidai.tania.ui.ExerciseScreen
import tn.ibtidai.tania.ui.HomeScreen
import tn.ibtidai.tania.ui.SplashScreen
import tn.ibtidai.tania.ui.TaniaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = ExerciseRepository(applicationContext)
        setContent {
            TaniaTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    App(repo)
                }
            }
        }
    }
}

@Composable
private fun App(repo: ExerciseRepository) {
    var subject by remember { mutableStateOf<Subject?>(null) }
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var contentVersion by remember { mutableIntStateOf(0) }
    var notice by remember { mutableStateOf<String?>(null) }
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500)
        showSplash = false
    }

    // عند فتح التطبيق: نتحقق من وجود تمارين جديدة على GitHub (بدون إنترنت نكمل بما هو موجود)
    LaunchedEffect(Unit) {
        val changes = withContext(Dispatchers.IO) { runCatching { repo.sync.sync() }.getOrDefault(0) }
        if (changes > 0) {
            contentVersion++
            notice = "🎉 وصلت تمارين جديدة!"
            delay(3000)
            notice = null
        }
    }

    BackHandler(enabled = subject != null) {
        if (exercise != null) exercise = null else subject = null
    }

    val s = subject
    val e = exercise
    Box(Modifier.fillMaxSize()) {
    Crossfade(targetState = showSplash, label = "splash") { splash ->
    if (splash) SplashScreen(onDone = { showSplash = false }) else
    when {
        s == null -> HomeScreen(onSubject = { subject = it })
        e == null -> ExerciseListScreen(
            subject = s,
            exercises = remember(s, contentVersion) { repo.list(s) },
            onBack = { subject = null },
            onOpen = { exercise = it },
        )
        else -> ExerciseScreen(subject = s, exercise = e, onBack = { exercise = null })
    }
    }
    notice?.let {
        Snackbar(Modifier.align(Alignment.BottomCenter).padding(16.dp)) { Text(it, fontSize = 18.sp) }
    }
    }
}

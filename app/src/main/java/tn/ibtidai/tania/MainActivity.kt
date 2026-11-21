package tn.ibtidai.tania

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import tn.ibtidai.tania.data.Exercise
import tn.ibtidai.tania.data.ExerciseRepository
import tn.ibtidai.tania.data.Subject
import tn.ibtidai.tania.ui.ExerciseListScreen
import tn.ibtidai.tania.ui.ExerciseScreen
import tn.ibtidai.tania.ui.HomeScreen
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

    BackHandler(enabled = subject != null) {
        if (exercise != null) exercise = null else subject = null
    }

    val s = subject
    val e = exercise
    when {
        s == null -> HomeScreen(onSubject = { subject = it })
        e == null -> ExerciseListScreen(
            subject = s,
            exercises = remember(s) { repo.list(s) },
            onBack = { subject = null },
            onOpen = { exercise = it },
        )
        else -> ExerciseScreen(subject = s, exercise = e, onBack = { exercise = null })
    }
}

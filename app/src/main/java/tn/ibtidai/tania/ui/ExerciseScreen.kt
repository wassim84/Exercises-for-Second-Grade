package tn.ibtidai.tania.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.ibtidai.tania.data.AnswerChecker
import tn.ibtidai.tania.data.Exercise
import tn.ibtidai.tania.data.Question
import tn.ibtidai.tania.data.Subject

private val TF_OPTIONS = listOf("صحيح", "خطأ")

private fun optionsOf(q: Question) = if (q.type == "truefalse") TF_OPTIONS else q.options

@Composable
fun ExerciseScreen(subject: Subject, exercise: Exercise, onBack: () -> Unit) {
    val answers = remember(exercise.id) { mutableStateListOf(*Array(exercise.questions.size) { "" }) }
    var submitted by remember(exercise.id) { mutableStateOf(false) }

    Scaffold(topBar = { TaniaTopBar(exercise.title, Color(subject.color), onBack) }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (exercise.instructions.isNotBlank()) {
                Text(exercise.instructions, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            exercise.text?.let {
                Text(
                    it,
                    fontSize = 20.sp,
                    lineHeight = 34.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(16.dp),
                )
            }

            exercise.questions.forEachIndexed { i, q ->
                QuestionCard(
                    number = i + 1,
                    question = q,
                    value = answers[i],
                    submitted = submitted,
                    onChange = { answers[i] = it },
                )
            }

            if (submitted) {
                val graded = exercise.questions.indices.filter { exercise.questions[it].type != "open" }
                val score = graded.count { AnswerChecker.isCorrect(exercise.questions[it], answers[it]) }
                val all = score == graded.size
                Text(
                    if (all) "🌟 أحسنت! $score من ${graded.size}" else "👍 $score من ${graded.size}، حاول مرة أخرى",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (all) Correct else Color(0xFFEF6C00),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Button(
                    onClick = {
                        for (k in answers.indices) answers[k] = ""
                        submitted = false
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) { Text("🔄 أعد المحاولة", fontSize = 20.sp) }
            } else {
                Button(
                    onClick = { submitted = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Correct),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                ) { Text("✅ تحقّق من إجاباتي", fontSize = 20.sp) }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    number: Int,
    question: Question,
    value: String,
    submitted: Boolean,
    onChange: (String) -> Unit,
) {
    val isOpen = question.type == "open"
    val correct = submitted && !isOpen && AnswerChecker.isCorrect(question, value)
    val wrong = submitted && !isOpen && !correct
    val borderColor = when {
        correct -> Correct
        wrong -> Wrong
        else -> Color(0xFFDDDDDD)
    }
    val bg = when {
        correct -> CorrectBg
        wrong -> WrongBg
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("$number) ${question.prompt}", fontSize = 21.sp, lineHeight = 32.sp)

        when (question.type) {
            "choice", "truefalse" -> {
                val opts = optionsOf(question)
                FlowRowOptions(opts, value, submitted, question, onChange)
            }
            else -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = { if (!submitted) onChange(it) },
                    readOnly = submitted,
                    singleLine = !isOpen,
                    minLines = if (isOpen) 4 else 1,
                    textStyle = TextStyle(fontSize = 22.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (submitted) borderColor else MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (submitted) borderColor else Color.Gray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    placeholder = { Text("اكتب إجابتك هنا") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (wrong) {
            Text(
                "✗ الإجابة الصحيحة: ${question.answers.first()}",
                color = Wrong, fontSize = 19.sp, fontWeight = FontWeight.Bold,
            )
        } else if (correct) {
            Text("✓ صحيح", color = Correct, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        } else if (submitted && isOpen && question.answers.isNotEmpty()) {
            Text("نموذج إجابة: ${question.answers.first()}", color = Model, fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowOptions(
    options: List<String>,
    selected: String,
    submitted: Boolean,
    question: Question,
    onSelect: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { opt ->
            val isSelected = opt == selected
            val isRight = AnswerChecker.isCorrect(question, opt)
            val (bg, fg) = when {
                submitted && isRight -> Correct to Color.White
                submitted && isSelected -> Wrong to Color.White
                isSelected -> MaterialTheme.colorScheme.primary to Color.White
                else -> Color.White to Color.Black
            }
            Text(
                opt,
                color = fg,
                fontSize = 20.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, if (isSelected || (submitted && isRight)) bg else Color.Gray, RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(enabled = !submitted) { onSelect(opt) }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            )
        }
    }
}

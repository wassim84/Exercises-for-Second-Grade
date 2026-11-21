package tn.ibtidai.tania.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.ibtidai.tania.R
import tn.ibtidai.tania.data.Exercise
import tn.ibtidai.tania.data.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaniaTopBar(title: String, color: Color, onBack: (() -> Unit)?) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 22.sp) },
        navigationIcon = {
            if (onBack != null) {
                TextButton(onClick = onBack) { Text("→ رجوع", color = Color.White, fontSize = 18.sp) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = color,
            titleContentColor = Color.White,
        ),
    )
}

@Composable
fun HomeScreen(onSubject: (Subject) -> Unit) {
    Scaffold(topBar = { TaniaTopBar("السنة الثانية ابتدائي", Color(0xFF3F51B5), null) }) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier = Modifier.size(84.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("مرحبًا يا بطل! 👋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("اختر مادّة وابدأ التمرين", fontSize = 18.sp, color = Color.Gray)
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(Subject.entries.toList()) { s ->
                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(s.color))
                        .clickable { onSubject(s) }
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(s.emoji, fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(s.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }
        }
    }
    }
}

@Composable
fun ExerciseListScreen(
    subject: Subject,
    exercises: List<Exercise>,
    onBack: () -> Unit,
    onOpen: (Exercise) -> Unit,
) {
    Scaffold(topBar = { TaniaTopBar(subject.title, Color(subject.color), onBack) }) { pad ->
        if (exercises.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد تمارين بعد", fontSize = 20.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(pad).fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(exercises) { ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color(subject.color), RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .clickable { onOpen(ex) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(subject.emoji, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(ex.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("${ex.questions.size} أسئلة", fontSize = 15.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

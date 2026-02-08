package com.example.sakina.ui.Checklist

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlin.random.Random

/* ---------------- BACKGROUND ---------------- */

@Composable
fun GalaxyBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_and_meteors")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(100000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "xOffset"
    )
    val meteorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "meteorProgress"
    )


    val meteorData = remember {
        List(8) {
            Triple(
                Random.nextFloat(), // X Start
                Random.nextFloat(), // Y Start
                Random.nextFloat() * 0.5f + 0.5f // Speed factor (عشوائية السرعة)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF020617))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = java.util.Random(42)

            val nebulaColor = Color(0xFF1E293B).copy(alpha = 0.3f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(nebulaColor, Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height * 0.3f),
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.2f, size.height * 0.3f)
            )
            repeat(150) {
                val baseX = random.nextFloat() * size.width
                val baseY = random.nextFloat() * size.height
                val currentX = (baseX + xOffset) % size.width
                val currentY = (baseY + (xOffset * 0.5f)) % size.height

                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat() * 0.4f),
                    radius = random.nextFloat() * 1.5.dp.toPx(),
                    center = Offset(currentX, currentY)
                )
            }
            meteorData.forEachIndexed { i, data ->
                val startDelay = i * 0.12f
                val individualProgress = (meteorProgress - startDelay).coerceIn(0f, 1f)

                if (individualProgress > 0f && individualProgress < 1f) {
                    val startXPos = data.first * size.width
                    val startYPos = data.second * size.height * 0.5f
                    val speed = data.third
                    val distance = size.width * 0.8f * individualProgress * speed

                    val currentMeteorX = startXPos + distance
                    val currentMeteorY = startYPos + (distance * 0.4f)

                    val alpha = if (individualProgress < 0.2f) {
                        individualProgress / 0.2f
                    } else {
                        (1f - individualProgress) / 0.8f
                    }.coerceIn(0f, 1f) * 0.5f
                    val tailBrush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = alpha), Color.Transparent),
                        start = Offset(currentMeteorX, currentMeteorY),
                        end = Offset(currentMeteorX - (100f * speed), currentMeteorY - (40f * speed))
                    )

                    drawLine(
                        brush = tailBrush,
                        start = Offset(currentMeteorX, currentMeteorY),
                        end = Offset(currentMeteorX - (110f * speed), currentMeteorY - (45f * speed)),
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = (1.2.dp.toPx() * speed),
                        center = Offset(currentMeteorX, currentMeteorY)
                    )
                }
            }
        }
        content()
    }
}
/* ---------------- SCREEN ---------------- */

@Composable
fun ChecklistScreen(

    viewModel: ChecklistViewModel = hiltViewModel()
) {  var showAddDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val streakDays by viewModel.streakDays
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
            ) {

                item { HeaderCard(streakDays = streakDays) }

                item {
                    ProgressCard(
                        completed = tasks.count { it.isCompleted },
                        total = tasks.size
                    )
                }
                item {
                    AddTaskCard(
                        onAddClick = { showAddDialog = true }
                    )
                }

                item {
                    Text(
                        text = "المهام اليومية",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
            if (showAddDialog) {
                AddTaskDialog(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    onAdd = {
                        viewModel.addNewTask(newTaskText)
                        newTaskText = ""
                        showAddDialog = false
                    },
                    onDismiss = { showAddDialog = false }
                )
            }
        }
    }
}

/* ---------------- HEADER ---------------- */

@Composable
private fun HeaderCard(streakDays: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFC107).copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color(0xFFFFC107),
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✨ هنعمل ايه انهارده؟",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
               text = "🔥 $streakDays يوم",
                color = Color(0xFFFFC107),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/* ---------------- PROGRESS ---------------- */

@Composable
private fun ProgressCard(
    completed: Int,
    total: Int
) {
    val progress = if (total == 0) 0f else completed / total.toFloat()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "التقدم اليومي",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$completed / $total",
                color = Color(0xFF64FFDA)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF64FFDA),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}


/* ---------------- TASK CARD ---------------- */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: ChecklistEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(18.dp)
            )
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* optional */ },
                onLongClick = { onDelete() }
            )

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF64FFDA),
                    uncheckedColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = task.taskName,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        Color.White.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AddTaskCard(
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color(0xFF64FFDA),
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.06f)
        ),
        onClick = onAddClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "＋",
                fontSize = 22.sp,
                color = Color(0xFF64FFDA),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إضافة مهمة جديدة",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Composable
fun AddTaskDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF020617).copy(alpha = 0.95f),
        shape = RoundedCornerShape(24.dp),

        title = {
            Text(
                text = "✨ مهمة جديدة",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },

        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "اكتب المهمة هنا…",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF64FFDA),
                    focusedBorderColor = Color(0xFF64FFDA),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                )
            )
        },

        confirmButton = {
            TextButton(onClick = onAdd) {
                Text(
                    "إضافة",
                    color = Color(0xFF64FFDA),
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "إلغاء",
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    )
}
/* ---------------- PREVIEW ---------------- */
@Preview(showBackground = true, heightDp = 800)
@Composable
fun ChecklistPreview() {

    val previewTasks = listOf(
        ChecklistEntity(1, "صدقة", false),
        ChecklistEntity(2, "ورد القرآن", true),
        ChecklistEntity(3, "قيام الليل", false)
    )

    var showAddDialog by remember { mutableStateOf(true) }
    var newTaskText by remember { mutableStateOf("ورد القرآن") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            Box {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { HeaderCard(streakDays = 12) }
                    item { ProgressCard(1, 3) }

                    items(previewTasks) {
                        TaskCard(
                            task = it,
                            onToggle = {},
                            onDelete = {}
                        )
                    }

                    item {
                        AddTaskCard(
                            onAddClick = { showAddDialog = true }
                        )
                    }
                }

            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddTaskDialogPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            AddTaskDialog(
                value = "ورد القرآن",
                onValueChange = {},
                onAdd = {},
                onDismiss = {}
            )
        }
    }
}


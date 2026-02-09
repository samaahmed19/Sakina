package com.example.sakina.ui.checklist

import androidx.compose.animation.core.*
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakina.data.local.database.entity.ChecklistEntity
import kotlin.random.Random

@Composable
fun GalaxyBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2000f,
        animationSpec = infiniteRepeatable(tween(100000, easing = LinearEasing), RepeatMode.Restart),
        label = "x"
    )

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A))))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = java.util.Random(42)
            repeat(150) {
                val baseX = random.nextFloat() * size.width
                val baseY = random.nextFloat() * size.height
                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat() * 0.4f),
                    radius = 1.5.dp.toPx(),
                    center = Offset((baseX + xOffset) % size.width, (baseY + (xOffset * 0.5f)) % size.height)
                )
            }
        }
        content()
    }
}

@Composable
fun ChecklistScreen(viewModel: ChecklistViewModel = hiltViewModel()) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }

    val tasks by viewModel.tasks.collectAsState()
    val streakState by viewModel.streak.collectAsState()
    val streakDaysCount by viewModel.streakDaysCount

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
            ) {
                item { HeaderCard(streakDays = streakDaysCount) }

                item {
                    ProgressCard(
                        completed = tasks.count { it.isCompleted },
                        total = tasks.size
                    )
                }

                item { AddTaskCard(onAddClick = { showAddDialog = true }) }

                item {
                    Text("المهام اليومية", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                items(tasks, key = { it.id }) { task ->
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
                        viewModel.addTask(newTaskText)
                        newTaskText = ""
                        showAddDialog = false
                    },
                    onDismiss = { showAddDialog = false }
                )
            }
        }
    }
}

@Composable
private fun HeaderCard(streakDays: Int) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107).copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFFFC107).copy(alpha = 0.5f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✨ هنعمل ايه انهارده؟", color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("$streakDays يوم", color = Color(0xFFFFC107), fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    val progress = if (total == 0) 0f else completed.toFloat() / total.toFloat()
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("التقدم اليومي", color = Color.White, fontWeight = FontWeight.Bold)
            Text("$completed / $total", color = Color(0xFF64FFDA))
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = Color(0xFF64FFDA),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(task: ChecklistEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .combinedClickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}, onLongClick = onDelete)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF64FFDA)))
            Text(task.taskName, color = Color.White, fontSize = 18.sp, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
            Text("⭐", fontSize = 14.sp)
        }
    }
}

@Composable
private fun AddTaskCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF64FFDA), RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        onClick = onAddClick
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("＋ إضافة مهمة جديدة", color = Color(0xFF64FFDA), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddTaskDialog(value: String, onValueChange: (String) -> Unit, onAdd: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = { Text("✨ مهمة جديدة", color = Color.White) },
        text = {
            OutlinedTextField(
                value = value, onValueChange = onValueChange,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
        },
        confirmButton = { TextButton(onClick = onAdd) { Text("إضافة", color = Color(0xFF64FFDA)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء", color = Color.White) } }
    )
}
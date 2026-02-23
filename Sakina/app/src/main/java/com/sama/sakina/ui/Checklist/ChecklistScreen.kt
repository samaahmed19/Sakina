package com.sama.sakina.ui.checklist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sama.sakina.data.local.database.entity.ChecklistEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun GalaxyBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020617), Color(0xFF0F172A))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = java.util.Random(42)
            repeat(150) {
                val x = random.nextFloat() * size.width
                val y = random.nextFloat() * size.height
                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat() * 0.4f),
                    radius = 1.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        content()
    }
}

private val Categories = listOf("عام", "صلاة", "قرآن", "أذكار", "رياضة", "شغل", "مذاكرة")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val streak by viewModel.streakDays.collectAsState()

    // Sheets
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("عام") }
    var editingTask by remember { mutableStateOf<ChecklistEntity?>(null) }

    val completed = remember(tasks) { tasks.count { it.isCompleted } }
    val total = tasks.size

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 34.dp, bottom = 40.dp)
            ) {
                item { HeaderSection(total = total, completed = completed, streak = streak) }

                item { ProgressCard(completed = completed, total = total) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AddTaskCard(
                            modifier = Modifier.weight(1f),
                            onAddClick = {
                                text = ""
                                selectedCategory = "عام"
                                showAddSheet = true
                            }
                        )
                        OutlinedButton(
                            onClick = { viewModel.deleteCompleted() },
                            modifier = Modifier.height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF64FFDA)
                            )
                        ) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "المهام اليومية",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF64FFDA),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "السجل",
                                color = Color(0xFF64FFDA),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (tasks.isEmpty()) {
                    item {
                        EmptyStateCard(
                            onAddClick = {
                                text = ""
                                selectedCategory = "عام"
                                showAddSheet = true
                            }
                        )
                    }
                } else {
                    items(tasks, key = { it.id }) { task ->
                        SwipeTaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        ) {
                            // onLongPress -> Edit
                            editingTask = task
                            text = task.taskName
                            selectedCategory = task.category
                            showEditSheet = true
                        }

                        // Move up / down (احترافي وسهل)
                        MoveRow(
                            onUp = { viewModel.moveUp(task) },
                            onDown = { viewModel.moveDown(task) }
                        )
                    }
                }
            }

            // Add Sheet
            if (showAddSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showAddSheet = false
                        text = ""
                        selectedCategory = "عام"
                    },
                    sheetState = sheetState,
                    containerColor = Color(0xFF0B1220),
                    tonalElevation = 0.dp,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.35f)) }
                ) {
                    TaskSheet(
                        title = "✨ مهمة جديدة",
                        value = text,
                        category = selectedCategory,
                        onValueChange = { text = it },
                        onCategoryChange = { selectedCategory = it },
                        onConfirmText = "إضافة",
                        onConfirm = {
                            val t = text.trim()
                            if (t.isNotEmpty()) {
                                viewModel.addTask(t, selectedCategory)
                                showAddSheet = false
                                text = ""
                                selectedCategory = "عام"
                            }
                        },
                        onCancel = {
                            showAddSheet = false
                            text = ""
                            selectedCategory = "عام"
                        }
                    )
                }
            }

            // Edit Sheet
            if (showEditSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showEditSheet = false
                        editingTask = null
                        text = ""
                        selectedCategory = "عام"
                    },
                    sheetState = sheetState,
                    containerColor = Color(0xFF0B1220),
                    tonalElevation = 0.dp,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.35f)) }
                ) {
                    TaskSheet(
                        title = "✏️ تعديل المهمة",
                        value = text,
                        category = selectedCategory,
                        onValueChange = { text = it },
                        onCategoryChange = { selectedCategory = it },
                        onConfirmText = "حفظ",
                        onConfirm = {
                            val task = editingTask ?: return@TaskSheet
                            val t = text.trim()
                            if (t.isNotEmpty()) {
                                viewModel.editTask(task, t)
                                viewModel.setCategory(task, selectedCategory)
                            }
                            showEditSheet = false
                            editingTask = null
                            text = ""
                            selectedCategory = "عام"
                        },
                        onCancel = {
                            showEditSheet = false
                            editingTask = null
                            text = ""
                            selectedCategory = "عام"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(total: Int, completed: Int, streak: Int) {
    var timeLeft by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            timeLeft = getTimeUntilMidnight()
            delay(60_000)
        }
    }

    val subtitle = when {
        total == 0 -> "ابدأ بمهمة واحدة بسيطة النهارده"
        completed == total && total != 0 -> "يوم مثالي… كمل كده 👏"
        completed > 0 -> "مستواك حلو… خلّص الباقي"
        else -> "لسه مبدأتش… شغّل مود الإنتاج"
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFC107).copy(alpha = 0.14f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFFC107).copy(alpha = 0.45f), RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("✨ مهام يومك", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFF64FFDA), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Streak: $streak يوم", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⏳ باقي على يوم جديد:", color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp)
                Spacer(Modifier.width(8.dp))
                Text(timeLeft, color = Color(0xFFFFC107), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun getTimeUntilMidnight(): String {
    val now = LocalDateTime.now()
    val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
    val duration = Duration.between(now, midnight)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    return "$hours ساعة و $minutes دقيقة"
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    val target = if (total == 0) 0f else completed.toFloat() / total.toFloat()
    val progress by animateFloatAsState(targetValue = target.coerceIn(0f, 1f), label = "progress")
    val percent = (target * 100).toInt().coerceIn(0, 100)

    val message = when {
        total == 0 -> "ابدأ بإضافة مهامك 🌿"
        completed == total && total != 0 -> "ما شاء الله خلصت يومك 👏"
        completed >= (total + 1) / 2 -> "كمل… إنت قريب 💪"
        else -> "لسه قدامك شوية 🔥"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("التقدم اليومي", color = Color.White, fontWeight = FontWeight.Bold)
                Text("$percent%", color = Color(0xFF64FFDA), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(if (total == 0) "0 / 0" else "$completed / $total", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(message, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(7.dp),
                color = Color(0xFF64FFDA),
                trackColor = Color.White.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
private fun AddTaskCard(modifier: Modifier = Modifier, onAddClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(54.dp)
            .border(1.dp, Color(0xFF64FFDA), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        onClick = onAddClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color(0xFF64FFDA), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("إضافة مهمة", color = Color(0xFF64FFDA), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun EmptyStateCard(onAddClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.TaskAlt, contentDescription = null, tint = Color(0xFF64FFDA), modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(10.dp))
            Text("لسه مفيش مهام النهارده", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text("ابدأ بمهمة واحدة بسيطة… وهتتفاجئ بإنجازك", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64FFDA)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("أضف أول مهمة", color = Color(0xFF06111F), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MoveRow(onUp: () -> Unit, onDown: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onUp) {
            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = Color.White.copy(alpha = 0.75f))
        }
        IconButton(onClick = onDown) {
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = Color.White.copy(alpha = 0.75f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun SwipeTaskItem(
    task: ChecklistEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onLongPressEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> { onToggle(); false }
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); true }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val dir = dismissState.dismissDirection
            val isToggle = dir == SwipeToDismissBoxValue.StartToEnd
            val isDelete = dir == SwipeToDismissBoxValue.EndToStart

            val bg = when {
                isToggle -> Color(0xFF0EA5E9).copy(alpha = 0.20f)
                isDelete -> Color(0xFFEF4444).copy(alpha = 0.20f)
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .background(bg, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp),
                contentAlignment = if (isDelete) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                val icon = if (isDelete) Icons.Filled.Delete else Icons.Filled.CheckCircle
                val tint = if (isDelete) Color(0xFFEF4444) else Color(0xFF0EA5E9)

                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }
        },
        content = {
            TaskCard(
                task = task,
                onToggle = onToggle,
                onDelete = onDelete,
                onLongPressEdit = onLongPressEdit
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    task: ChecklistEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onLongPressEdit: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onToggle() },
                onLongClick = { onLongPressEdit() }
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF64FFDA))
            )

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            ) {
                Text(
                    task.taskName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "الفئة: ${task.category}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.75f))
            }
        }
    }
}

@Composable
private fun TaskSheet(
    title: String,
    value: String,
    category: String,
    onValueChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onConfirmText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 18.dp)
    ) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("اكتب اسم المهمة…", color = Color.White.copy(alpha = 0.5f)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF64FFDA),
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF64FFDA)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Text("اختار فئة:", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))

        FlowRowCategories(
            selected = category,
            onSelect = onCategoryChange
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("إلغاء", color = Color.White.copy(alpha = 0.85f))
            }

            Button(
                onClick = onConfirm,
                enabled = value.trim().isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64FFDA)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(onConfirmText, color = Color(0xFF06111F), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FlowRowCategories(
    selected: String,
    onSelect: (String) -> Unit
) {
    // FlowRow بدون dependency: نعملها Row + Wrap بسيط باستخدام Column
    // عشان ما نزودش مكتبات على مشروعك.
    val rows = remember { Categories.chunked(3) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    FilterChip(
                        selected = selected == cat,
                        onClick = { onSelect(cat) },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF64FFDA),
                            selectedLabelColor = Color(0xFF06111F),
                            containerColor = Color.White.copy(alpha = 0.06f),
                            labelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
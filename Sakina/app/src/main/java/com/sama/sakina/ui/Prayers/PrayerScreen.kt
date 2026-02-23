package com.sama.sakina.ui.Prayers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.sama.sakina.domain.model.Prayer
import com.sama.sakina.domain.model.PrayerCalculationMethod
import com.sama.sakina.domain.model.PrayerDaySummary
import com.sama.sakina.domain.model.PrayerKey
import com.sama.sakina.domain.model.PrayerMadhab
import com.sama.sakina.domain.model.PrayerSettings
import com.sama.sakina.domain.model.PrayerType
import com.sama.sakina.domain.model.ZawalStatus
import com.sama.sakina.utils.formatClockTime
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun PrayerScreen(viewModel: PrayerViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    PrayerTreeContent(
        uiState = uiState,
        selectedDate = selectedDate,
        onDateSelected = { viewModel.onDateSelected(it) },
        onToggle = { key, newChecked -> viewModel.setPrayerChecked(key, newChecked) },
        onSetMethod = { viewModel.setCalculationMethod(it) },
        onSetMadhab = { viewModel.setMadhab(it) },
        onRetry = { viewModel.load() }
    )
}

@Composable
fun PrayerTreeContent(
    uiState: PrayerUiState,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onToggle: (PrayerKey, Boolean) -> Unit,
    onSetMethod: (PrayerCalculationMethod) -> Unit,
    onSetMadhab: (PrayerMadhab) -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    var showCelebrate by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // إظهار الاحتفال لما كل الفروض تكتمل
    LaunchedEffect(uiState.summary?.shouldCelebrate) {
        if (uiState.summary?.shouldCelebrate == true) {
            showCelebrate = true
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF020617), Color(0xFF0F172A))))
        ) {
            StarsBackground()

            if (showCelebrate) {
                CelebrationDialog(
                    onShare = {
                        val text = "الحمد لله، أتممت صلوات اليوم الخمس 🎉\n\n" +
                                "جرّب تتبُّع صلاتك وتنظيم عبادتك مع تطبيق سكينة."
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, text)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "مشاركة الإنجاز")
                        context.startActivity(shareIntent)
                    },
                    onClose = { showCelebrate = false }
                )
            }

            if (showSettings) {
                PrayerSettingsDialog(
                    settings = uiState.settings,
                    onSetMethod = onSetMethod,
                    onSetMadhab = onSetMadhab,
                    onClose = { showSettings = false }
                )
            }

            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorCard(message = uiState.error, onRetry = onRetry)
                uiState.summary == null -> EmptyState()
                else -> {
                    val summary = uiState.summary
                    val fard = summary.items.filter { it.type == PrayerType.FARD }
                    val nawafil = summary.items.filter { it.type == PrayerType.NAFILA }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        contentPadding = PaddingValues(top = 22.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            HeaderSection(
                                summary = summary,
                                settings = uiState.settings,
                                onOpenSettings = { showSettings = true }
                            )
                        }

                        item { AyahCard() }

                        // الميزة الجديدة: الكاليندر
                        item {
                            PrayerCalendarSection(
                                monthlyData = uiState.monthlyCompletion, // تأكد إن دي موجودة في الـ UiState
                                selectedDate = selectedDate,
                                onDateSelected = onDateSelected
                            )
                        }

                        item { SectionTitle("الفرائض") }
                        items(fard, key = { it.key.key }) { item ->
                            PrayerCard(
                                prayer = item,
                                timeMillis = uiState.fardPrayerTimes[item.key],
                                accent = Color(0xFFFFD700),
                                onToggle = onToggle
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                            SectionTitle("النوافل")
                        }
                        items(nawafil, key = { it.key.key }) { item ->
                            PrayerCard(
                                prayer = item,
                                timeMillis = null,
                                accent = Color(0xFFB388FF),
                                onToggle = onToggle
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ----------------------------- Calendar Section ----------------------------- */
@Composable
fun PrayerCalendarSection(
    monthlyData: Map<String, Int>,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val monthDisplayFormat = remember { SimpleDateFormat("MMMM yyyy", Locale("ar")) }
    val todayDateStr = remember { sdf.format(Date()) }
    val todayDate = remember { sdf.parse(todayDateStr)!! }

    var currentMonthCal by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotEmpty()) {
            val date = sdf.parse(selectedDate)
            if (date != null) {
                val cal = Calendar.getInstance().apply { time = date }
                if (cal.get(Calendar.MONTH) != currentMonthCal.get(Calendar.MONTH) ||
                    cal.get(Calendar.YEAR) != currentMonthCal.get(Calendar.YEAR)) {
                    currentMonthCal = cal
                }
            }
        }
    }

    val gridData = remember(currentMonthCal) {
        val daysInMonth = currentMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfMonthCal = (currentMonthCal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val firstDayOfWeek = firstDayOfMonthCal.get(Calendar.DAY_OF_WEEK)
        val offset = (firstDayOfWeek - Calendar.SATURDAY + 7) % 7
        val prefix = SimpleDateFormat("yyyy-MM-", Locale.US).format(currentMonthCal.time)
        val daysList = List(offset) { -1 } + (1..daysInMonth).toList()
        Triple(daysList.chunked(7), prefix, daysInMonth)
    }

    val weeks = gridData.first
    val yearMonthPrefix = gridData.second

    val goldColor = Color(0xFFFFD700)
    val purpleColor = Color(0xFFB388FF)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.02f)),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(goldColor, purpleColor, goldColor)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = goldColor.copy(alpha = 0.2f),
                ambientColor = purpleColor.copy(alpha = 0.2f)
            )
    ) {

        Column(modifier = Modifier.padding(12.dp)) {
            // سطر التحكم (الشهر والأسهم)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonthCal = (currentMonthCal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White) }

                Text(monthDisplayFormat.format(currentMonthCal.time), color = Color.White, fontWeight = FontWeight.Bold)

                IconButton(onClick = {
                    currentMonthCal = (currentMonthCal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                }) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White) }
            }

            // سطر الحروف
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), Arrangement.SpaceAround) {
                listOf("س", "ح", "ن", "ث", "ر", "خ", "ج").forEach { dayLetter ->
                    Text(text = dayLetter, color = Color.White.copy(0.4f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // شبكة الأيام
            weeks.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                    week.forEach { day ->
                        if (day == -1) {
                            Spacer(Modifier.size(45.dp))
                        } else {
                            val dateKey = yearMonthPrefix + String.format(Locale.US, "%02d", day)
                            val isFuture = remember(dateKey) { sdf.parse(dateKey)?.after(todayDate) ?: false }
                            val rawData = monthlyData[dateKey] ?: 0

                            DayCell(
                                day = day,
                                fardCount = rawData % 10,
                                nafilaCount = rawData / 10,
                                isSelected = (dateKey == selectedDate),
                                isToday = (dateKey == todayDateStr),
                                isFuture = isFuture,
                                onClick = { onDateSelected(dateKey) }
                            )
                        }
                    }
                    if (week.size < 7) {
                        repeat(7 - week.size) { Spacer(Modifier.size(45.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    fardCount: Int,
    nafilaCount: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isFuture: Boolean,
    onClick: () -> Unit
) {
    val dayNumberColor = remember(isFuture, fardCount, nafilaCount) {
        when {
            isFuture -> Color.White.copy(alpha = 0.3f)
            fardCount >= 5 -> {
                when {
                    nafilaCount >= 3 -> Color(0xFFFFD700)
                    nafilaCount == 2 -> Color(0xFFC0C0C0)
                    nafilaCount == 1 -> Color(0xFFCD7F32)
                    else -> Color.White
                }
            }
            else -> Color.White
        }
    }

    val animatedNumberColor by animateColorAsState(targetValue = dayNumberColor, label = "color")

    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White.copy(0.15f) else if (isToday) Color.White.copy(0.08f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.5.dp else if (isToday) 1.dp else 0.dp,
                color = if (isSelected) Color.White.copy(0.6f) else if (isToday) Color.White.copy(0.2f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable(enabled = !isFuture) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (fardCount > 0 && !isFuture) {
            Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                val strokeWidth = 2.dp.toPx()
                val ringColor = Color(0xFF4CAF50)

                if (fardCount >= 5) {
                    drawCircle(color = ringColor, style = Stroke(width = strokeWidth))
                } else {
                    val sweepAngle = (fardCount / 5.0f) * 360f
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Text(
            text = day.toString(),
            color = animatedNumberColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

/* ----------------------------- Old Beautiful Components ----------------------------- */
@Composable
private fun HeaderSection(
    summary: PrayerDaySummary,
    settings: PrayerSettings,
    onOpenSettings: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "صلاتي", color = Color(0xFFFFD700), fontSize = 35.sp)
            TextButton(onClick = onOpenSettings) {
                Text("الإعدادات", color = Color.White.copy(alpha = 0.85f))
            }
        }
        Text(
            text = "${summary.completedFardCount} من ${summary.totalFardCount} صلوات",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )
        Text(
            text = "${settings.method.labelAr} • ${settings.madhab.labelAr}",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int) {
    val ratio = if (total == 0) 0f else (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("التقدم الكلي", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                Text("$completed / $total", color = Color(0xFFFFD700), fontSize = 16.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(ratio)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFB388FF))),
                            RoundedCornerShape(999.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun AyahCard() {
    val cyan = Color(0x8500CCFF)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = cyan.copy(alpha = 0.22f),
                spotColor = cyan.copy(alpha = 0.26f)
            )
            .background(cyan.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
            .border(
                BorderStroke(1.6.dp, Brush.verticalGradient(listOf(cyan, Color.Transparent))),
                RoundedCornerShape(24.dp)
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "﴿فَإِذَا قَضَيْتُمُ الصَّلَاةَ فَاذْكُرُوا اللَّهَ قِيَامًا وَقُعُودًا وَعَلَىٰ جُنُوبِكُمْ ۚ فَإِذَا اطْمَأْنَنتُمْ فَأَقِيمُوا الصَّلَاةَ ۚ إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "النساء: 103",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun PrayerCard(
    prayer: Prayer,
    timeMillis: Long?,
    accent: Color,
    onToggle: (PrayerKey, Boolean) -> Unit
) {
    val glowElevation by animateDpAsState(
        targetValue = if (prayer.isCompleted) 30.dp else 0.dp,
        label = "glowElevation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (prayer.isCompleted) 0.16f else 0.03f,
        label = "glowAlpha"
    )

    val borderBrush = Brush.linearGradient(
        colors = if (prayer.isCompleted) {
            listOf(accent.copy(alpha = 1f), accent.copy(alpha = 0.5f)) // تم التعديل عشان الشفافية ماتزيدش عن 1
        } else {
            listOf(accent.copy(alpha = 1f), Color.Transparent)
        }
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = if (prayer.isCompleted) 0.07f else 0.05f)
        ),
        border = BorderStroke(1.dp, borderBrush),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = glowElevation,
                shape = RoundedCornerShape(22.dp),
                ambientColor = accent.copy(alpha = glowAlpha),
                spotColor = accent.copy(alpha = glowAlpha)
            )
            .clickable { onToggle(prayer.key, !prayer.isCompleted) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    prayer.titleAr,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    if (prayer.type == PrayerType.FARD) {
                        val timeText = timeMillis?.let { formatClockTime(context, it) }
                        if (timeText != null) "فرض • $timeText" else "فرض"
                    } else {
                        "نافلة"
                    },
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp
                )
            }

            Checkbox(
                checked = prayer.isCompleted,
                onCheckedChange = { checked -> onToggle(prayer.key, checked) },
                colors = CheckboxDefaults.colors(
                    checkedColor = accent,
                    uncheckedColor = Color.White.copy(alpha = 0.4f),
                    checkmarkColor = Color.Black
                )
            )
        }
    }
}

@Composable
private fun PrayerSettingsDialog(
    settings: PrayerSettings,
    onSetMethod: (PrayerCalculationMethod) -> Unit,
    onSetMadhab: (PrayerMadhab) -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            border = BorderStroke(1.3.dp, Color(0xFFFFD700).copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("إعدادات المواقيت", color = Color(0xFFFFD700), fontSize = 20.sp)

                Text("طريقة الحساب", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
                PrayerCalculationMethod.entries.forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetMethod(method) }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = settings.method == method, onClick = { onSetMethod(method) })
                        Spacer(Modifier.width(8.dp))
                        Text(method.labelAr, color = Color.White.copy(alpha = 0.85f))
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text("المذهب (العصر)", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
                PrayerMadhab.entries.forEach { madhab ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSetMadhab(madhab) }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = settings.madhab == madhab, onClick = { onSetMadhab(madhab) })
                        Spacer(Modifier.width(8.dp))
                        Text(madhab.labelAr, color = Color.White.copy(alpha = 0.85f))
                    }
                }

                Spacer(Modifier.height(6.dp))

                TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
                    Text("إغلاق", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CelebrationDialog(
    onShare: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
            border = BorderStroke(1.3.dp, Color(0xFFFFD700).copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).padding(bottom = 8.dp),
                    tint = Color(0xFFFFD700)
                )
                Text("بارك الله فيك!", color = Color(0xFFFFD700), fontSize = 28.sp)
                Text("حافظتِ على صلواتك اليوم", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
                Text(
                    "شارك إنجازك وساعد غيرك يلتزم ويبدأ رحلته",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                    ) {
                        Text("مشاركة", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                        Text("إغلاق", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun StarsBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val rnd = Random(42)
        repeat(160) {
            val x = rnd.nextFloat() * size.width
            val y = rnd.nextFloat() * size.height
            val alpha = (rnd.nextFloat() * 0.9f).coerceIn(0.05f, 0.9f)
            val radius = (rnd.nextFloat() * 2.2f).coerceIn(0.4f, 2.2f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = alpha), Color.Transparent),
                    center = Offset(x, y),
                    radius = radius * 3f
                ),
                radius = radius * 3f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFFFFD700))
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("لا توجد بيانات بعد", color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.92f),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.35f)),
            modifier = Modifier.padding(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("حدث خطأ", color = Color.White, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                Text(message, color = Color.White.copy(alpha = 0.75f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "إعادة المحاولة",
                    color = Color(0xFFFFD700),
                    modifier = Modifier
                        .clickable { onRetry() }
                        .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
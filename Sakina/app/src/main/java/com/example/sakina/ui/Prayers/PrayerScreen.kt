package com.example.sakina.ui.Prayers

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import com.example.sakina.domain.model.Prayer
import com.example.sakina.domain.model.PrayerDaySummary
import com.example.sakina.domain.model.PrayerKey
import com.example.sakina.domain.model.PrayerType
import com.example.sakina.domain.model.ZawalStatus
import kotlin.random.Random

@Composable
fun PrayerScreen(
    viewModel: PrayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PrayerTreeContent(
        uiState = uiState,
        onToggle = { key, newChecked -> viewModel.setPrayerChecked(key, newChecked) },
        onRetry = { viewModel.load() }
    )
}

@Composable
fun PrayerTreeContent(
    uiState: PrayerUiState,
    onToggle: (PrayerKey, Boolean) -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    val summary = uiState.summary

    var showCelebrate by remember { mutableStateOf(false) }

    LaunchedEffect(summary?.shouldCelebrate) {
        if (summary?.shouldCelebrate == true) {
            showCelebrate = true
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF020617), Color(0xFF0F172A))
                    )
                )
        ) {
            StarsBackground()

            if (showCelebrate) {
                CelebrationDialog(
                    onShare = {
                        val text =
                            "الحمد لله، أتممت صلوات اليوم الخمس 🎉\n\n" +
                            "جرّب تتبُّع صلاتك وتنظيم عبادتك مع تطبيق سكينة."
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, text)
                            type = "text/plain"
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "مشاركة الإنجاز")
                        context.startActivity(shareIntent)
                    },
                    onClose = { showCelebrate = false }
                )
            }

            when {
                uiState.isLoading -> LoadingState()
                uiState.error != null -> ErrorCard(message = uiState.error, onRetry = onRetry)
                uiState.summary == null -> EmptyState()
                else -> PrayerList(summary = uiState.summary, onToggle = onToggle)
            }
        }
    }
}

/* ----------------------------- States ----------------------------- */

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("جاري التحميل...", color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("لا توجد بيانات بعد", color = Color.White.copy(alpha = 0.85f))
    }
}

/* ----------------------------- Main List ----------------------------- */

@Composable
private fun PrayerList(
    summary: PrayerDaySummary,
    onToggle: (PrayerKey, Boolean) -> Unit
) {
    val fard = summary.items.filter { it.type == PrayerType.FARD }
    val nawafil = summary.items.filter { it.type == PrayerType.NAFILA }

            LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        contentPadding = PaddingValues(top = 22.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { HeaderSection(summary = summary) }
        item { AyahCard() }

        item { SectionTitle("الفرائض") }
        items(fard, key = { it.key.key }) { item ->
            PrayerCard(
                prayer = item,
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
                accent = Color(0xFFB388FF),
                onToggle = onToggle
            )
        }
    }
}

/* ----------------------------- Header ----------------------------- */

@Composable
private fun HeaderSection(summary: PrayerDaySummary) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "صلاتي", color = Color.White, fontSize = 30.sp)

        Text(
            text = "${summary.completedFardCount} من ${summary.totalFardCount} صلوات",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 16.sp
        )

        ProgressCard(completed = summary.completedFardCount, total = summary.totalFardCount)
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

// Old banner kept only in git history; celebration is now handled by the dialog above.

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "بارك الله فيك!",
                    color = Color(0xFFFFD700),
                    fontSize = 28.sp
                )
                Text(
                    "حافظتِ على صلواتك اليوم",
                    color = Color.White,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    "شارك إنجازك وساعد غيرك يلتزم ويبدأ رحلته",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("مشاركة")
                    }
                    TextButton(
                        onClick = onClose,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إغلاق", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.92f),
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 6.dp)
    )
}

/* ----------------------------- Prayer Card (Glow when completed) ----------------------------- */

@Composable
private fun PrayerCard(
    prayer: Prayer,
    accent: Color,
    onToggle: (PrayerKey, Boolean) -> Unit
) {
    val glowElevation by animateDpAsState(
        // Softer glow so it looks smoother on different screens
        targetValue = if (prayer.isCompleted) 10.dp else 0.dp,
        label = "glowElevation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (prayer.isCompleted) 0.16f else 0.03f,
        label = "glowAlpha"
    )

    val borderBrush = Brush.linearGradient(
        colors = if (prayer.isCompleted) {
            listOf(accent.copy(alpha = 0.95f), accent.copy(alpha = 0.25f), Color.Transparent)
        } else {
            listOf(accent.copy(alpha = 0.65f), Color.Transparent)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    prayer.titleAr,
                    color = Color.White,
                    fontSize = 20.sp
                )
                Text(
                    if (prayer.type == PrayerType.FARD) "فرض" else "نافلة",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp
                )
            }

            Checkbox(
                checked = prayer.isCompleted,
                onCheckedChange = { checked -> onToggle(prayer.key, checked) }
            )
        }
    }
}

/* ----------------------------- Error UI ----------------------------- */

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

/* ----------------------------- Ayah Card ----------------------------- */

@Composable
fun AyahCard() {
    val cyan = Color(0x8500CCFF)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
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
                text = "﴿فَإِذَا قَضَيْتُمُ الصَّلَاةَ فَاذْكُرُوا اللَّهَ قِيَامًا وَقُعُودًا وَعَلَىٰ جُنُوبِكُمْ ۚ فَإِذَا اطْمَأْنَنتُمْ فَأَقِيمُوا الصَّلَاةَ ۚ إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا﴾",
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

/* ----------------------------- Stars Background ----------------------------- */

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

/* ----------------------------- Interactive Preview ----------------------------- */

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PrayerScreenInteractivePreview() {
    var summary by remember {
        mutableStateOf(
            PrayerDaySummary(
                date = "2026-02-02",
                items = listOf(
                    // Preview: first two are visibly completed, and all fard are completed so the popup shows immediately
                    Prayer(PrayerKey.PRAYER_FAJR, "الفجر", PrayerType.FARD, true),
                    Prayer(PrayerKey.PRAYER_DHUHR, "الظهر", PrayerType.FARD, true),
                    Prayer(PrayerKey.PRAYER_ASR, "العصر", PrayerType.FARD, true),
                    Prayer(PrayerKey.PRAYER_MAGHRIB, "المغرب", PrayerType.FARD, true),
                    Prayer(PrayerKey.PRAYER_ISHA, "العشاء", PrayerType.FARD, true),
                    Prayer(PrayerKey.NAFILA_DUHA, "الضحى", PrayerType.NAFILA, false),
                    Prayer(PrayerKey.NAFILA_WITR, "الوتر", PrayerType.NAFILA, false),
                    Prayer(PrayerKey.NAFILA_QIYAM, "قيام الليل", PrayerType.NAFILA, false)
                ),
                completedFardCount = 5,
                totalFardCount = 5,
                isAllFardCompleted = true,
                shouldCelebrate = true,
                motivationalText = "معاينة: كل الفرائض مكتملة",
                zawalStatus = ZawalStatus.Unknown
            )
        )
    }

    PrayerTreeContent(
        uiState = PrayerUiState(isLoading = false, summary = summary),
        onToggle = { key, newChecked ->
            val updatedItems = summary.items.map {
                if (it.key == key) it.copy(isCompleted = newChecked) else it
            }

            val updatedFard = updatedItems.filter { it.type == PrayerType.FARD }
            val completedFardCount = updatedFard.count { it.isCompleted }
            val totalFardCount = updatedFard.size
            val isAllFardCompleted = (totalFardCount != 0 && completedFardCount == totalFardCount)

            summary = summary.copy(
                items = updatedItems,
                completedFardCount = completedFardCount,
                totalFardCount = totalFardCount,
                isAllFardCompleted = isAllFardCompleted,
                shouldCelebrate = isAllFardCompleted
            )
        },
        onRetry = {}
    )
}

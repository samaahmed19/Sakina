package com.example.sakina.ui.HolyQuran.surah_details

import androidx.compose.runtime.getValue
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakina.R
import kotlinx.coroutines.delay
import java.util.Random
// ================= BACKGROUND =================
@Composable

fun GalaxyBackground(content: @Composable () -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "stars")

    val xOffset by infiniteTransition.animateFloat(

        initialValue = 0f,

        targetValue = 2000f,

        animationSpec = infiniteRepeatable(

            animation = tween(60000, easing = LinearEasing),

            repeatMode = RepeatMode.Restart

        ), label = "xOffset"

    )
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

            val random = Random(42)

            repeat(120) {

                val baseX = random.nextFloat() * size.width

                val baseY = random.nextFloat() * size.height

                val currentX = (baseX + xOffset) % size.width

                val currentY = (baseY + (xOffset * 0.5f)) % size.height


                drawCircle(

                    color = Color.White.copy(alpha = random.nextFloat()),

                    radius = random.nextFloat() * 1.8.dp.toPx(),

                    center = Offset(currentX, currentY)

                )

            }

        }

        content()

    }

}

// ================= MAIN SCREEN =================
@Composable
fun SurahDetailsScreen(
    surahId: Int,
    surahName: String,
    ayahCount: Int,
    viewModel: SurahDetailsViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val fontSize by viewModel.fontSize
    val isBookmarked by viewModel.isBookmarked
    val ayat by viewModel.ayatList.collectAsState()
    val expandedAyahIndex by viewModel.expandedAyahIndex
    val tafsirMap by viewModel.tafsirMap.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    val lastSavedIndex by viewModel.lastSavedAyahIndex


    LaunchedEffect(surahId) {
        viewModel.loadSurahData(surahId)
    }
    LaunchedEffect(ayat, lastSavedIndex) {
        if (ayat.isNotEmpty() && lastSavedIndex != null && lastSavedIndex != -1) {
            delay(600)
            listState.animateScrollToItem(
                index = lastSavedIndex!!,
                scrollOffset = -200
            )
        }
    }

    GalaxyBackground {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                SurahHeader(
                    surahName = surahName,
                    ayahCount = ayahCount,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = { viewModel.toggleBookmark() },
                    onSettingsClick = { showSettings = !showSettings }
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSettings,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                ) {
                    FontSizeCard(fontSize) { viewModel.updateFontSize(it) }
                }
                Spacer(Modifier.height(24.dp))
                if (surahId != 9) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = Color(0xFFFFD700),
                        fontSize = (fontSize + 4).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(items = ayat,
                        key = { _, ayah -> ayah.number }) { index, ayah ->
                        val currentTafsir = tafsirMap[ayah.number] ?: "جاري تحميل التفسير..."
                        AyahCard(
                            number = ayah.number,
                            text = ayah.text,
                            fontSize = fontSize,
                            isExpanded = expandedAyahIndex == index,
                            onCardClick = { viewModel.toggleAyahExpansion(index) },
                            tafsir = currentTafsir, // تمرير التفسير الفعلي
                            index=index,
                            isLastRead = (lastSavedIndex ?: -1) == index,
                            onBookmarkAyah = {
                                viewModel.saveLastRead(surahId, surahName, index, ayahCount)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SurahHeader(
    surahName: String,
    ayahCount: Int,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFFFD700).copy(alpha = 0.7f))
                .background(Color(0xFF0F172A), shape = RoundedCornerShape(24.dp))
                .border(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFD700).copy(alpha = 0.1f), Color(0xFFFFD700))), RoundedCornerShape(24.dp))
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "سورة $surahName", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = "$ayahCount آية", color = Color.LightGray.copy(alpha = 0.8f), fontSize = 13.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onSettingsClick() }
                        .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = "Aa",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onBookmarkClick() }
                        .border(1.dp, if (isBookmarked) Color(0xFFFFD700) else Color.Transparent, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isBookmarked) Color(0xFFFFD700) else Color.White
                    )
                }
            }
        }
    }
}
// ================= FONT SIZE CARD =================
@Composable
fun FontSizeCard(fontSize: Float, onChange: (Float) -> Unit) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "حجم الخط",
                color = Color.White,
                fontSize = 14.sp
            )

            Slider(
                value = fontSize,
                onValueChange = onChange,
                valueRange = 16f..36f,
                modifier = Modifier.height(30.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFFD700),
                    activeTrackColor = Color(0xFFFFD700)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("A-", color = Color(0xFF60A5FA), fontSize = 12.sp)
                Text("A+", color = Color(0xFF60A5FA), fontSize = 12.sp)
            }
        }
    }
}
// ================= AYAH CARD =================
@Composable
fun AyahCard(
    number: Int,
    text: String,
    fontSize: Float,
    isExpanded: Boolean,
    onCardClick: () -> Unit,
    tafsir: String,
    index: Int,
    isLastRead: Boolean,
    onBookmarkAyah: () -> Unit
) {
    val translationY = remember { Animatable(50f) }
    LaunchedEffect(Unit) {
        translationY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 500,
                delayMillis = (index * 50).coerceAtMost(500),
                easing = FastOutSlowInEasing
            )
        )
    }
    val glowColor = if (isLastRead) {
        Color(0xFFFFD700).copy(alpha = 0.5f)
    } else if (isExpanded) {
        Color(0xFFFFD700).copy(alpha = 0.4f)
    } else {
        Color(0xFFBFA14A).copy(alpha = 0.15f)
    }

    val annotatedString = buildAnnotatedString {
        append(text)
        append(" ")
        appendInlineContent("ayah_number", "[number]")
    }

    val inlineContent = mapOf(
        "ayah_number" to InlineTextContent(
            Placeholder(
                width = (fontSize * 1.5f).sp,
                height = (fontSize * 1.5f).sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            IslamicNumberCircle(number)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .graphicsLayer(translationY = translationY.value)
            .drawBehind {
                val shadowRadius = if (isLastRead || isExpanded) 40.dp.toPx() else 15.dp.toPx()
                drawIntoCanvas { canvas ->
                    val paint = androidx.compose.ui.graphics.Paint()
                    val frameworkPaint = paint.asFrameworkPaint()
                    frameworkPaint.color = glowColor.toArgb()
                    frameworkPaint.setShadowLayer(shadowRadius, 0f, 0f, glowColor.toArgb())

                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        20.dp.toPx(), 20.dp.toPx(), paint
                    )
                }
            }
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(
                width = if (isLastRead) 2.5.dp else 1.dp,
                color = if (isLastRead) Color(0xFFFFD700)
                else if (isExpanded) Color(0xFFFFD700).copy(alpha = 0.5f)
                else Color(0xFF1E293B),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onCardClick() }
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-8).dp, y = (-8).dp)
                .clip(RoundedCornerShape(bottomEnd = 12.dp))
                .clickable { onBookmarkAyah() }
                .padding(8.dp)
        ) {
            Icon(
                imageVector = if (isLastRead) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Save Progress",
                tint = if (isLastRead) Color(0xFFFFD700) else Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(22.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = annotatedString,
                inlineContent = inlineContent,
                color = Color.White,
                fontSize = fontSize.sp,
                lineHeight = (fontSize * 1.6f).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "التفسير الميسر:",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = tafsir,
                        color = Color(0xFFECECEC),
                        fontSize = (fontSize - 4).coerceAtLeast(14f).sp,
                        lineHeight = ((fontSize - 4).coerceAtLeast(14f) * 1.5f).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
// ================= NUMBER CIRCLE =================

@Composable
fun IslamicNumberCircle(number: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(38.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ayah_number),
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .alpha(0.8f)
        )

        Text(
            text = number.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}
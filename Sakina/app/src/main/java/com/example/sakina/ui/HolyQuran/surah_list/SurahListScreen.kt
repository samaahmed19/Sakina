package com.example.sakina.ui.HolyQuran.surah_list

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import com.example.sakina.R
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ================== DATA MODEL ==================

data class Surah(
    val number: Int,
    val nameAr: String,
    val nameEn: String,
    val ayahCount: Int,
    val type: String
)

// ================== BACKGROUND ==================

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
            val random = java.util.Random(42)
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
// ================== MAIN SCREEN ==================

@Composable
fun SurahListScreen(surahList: List<Surah>) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        GalaxyBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically ,)
                {

                    Text(
                        text = "القرآن الكريم",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.quran),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 8.dp, bottom = 8.dp)
                    )
                }

                Text(
                    text = "إقرأ وتدبر كلام الله",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFFFFF),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))
                SearchBar()
                Spacer(modifier = Modifier.height(14.dp))
                FilterChips()
                Spacer(modifier = Modifier.height(18.dp))
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(1000)
                            )
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(surahList) { surah ->
                            SurahCard(surah)
                        }
                    }
                }
            }
        }
    }
}
// ================== SEARCH ==================
@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 0.6f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        interactionSource = interactionSource,
        placeholder = { Text("ابحث عن سورة...", color = Color.Gray) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = if (isFocused) Color(0xFFFFD700) else Color.Gray
            )
        },
        shape = RoundedCornerShape(30.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFF334155),
            focusedBorderColor = Color(0xFFFFD700),
            cursorColor = Color(0xFFFFD700),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isFocused) 15.dp else 0.dp,
                shape = RoundedCornerShape(30.dp),
                spotColor = Color(0xFFFFD700).copy(alpha = glowAlpha),
                ambientColor = Color(0xFFFFD700).copy(alpha = glowAlpha)
            )
    )
}
// ================== FILTER CHIPS ==================

@Composable
fun FilterChips() {
    var selectedFilter by remember { mutableStateOf("الكل") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            FilterChipItem(
                text = "الكل",
                selected = selectedFilter == "الكل",
                selectedColor = Color(0xFFFFD700),
                onSelected = { selectedFilter = "الكل" }
            )

            FilterChipItem(
                text = "مدنية",
                selected = selectedFilter == "مدنية",
                selectedColor = Color(0xFF81A9F9),
                onSelected = { selectedFilter = "مدنية" }
            )

            FilterChipItem(
                text = "مكية",
                selected = selectedFilter == "مكية",
                selectedColor = Color(0xFF4FD9C7),
                onSelected = { selectedFilter = "مكية" }
            )

            FilterChipItem(
                text = "قصيرة",
                selected = selectedFilter == "قصيرة",
                selectedColor = Color(0xFFB59FF4),
                onSelected = { selectedFilter = "قصيرة" }
            )
        }
    }
}
@Composable
fun FilterChipItem(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onSelected: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) selectedColor else Color(0xFF1E293B),
        animationSpec = tween(durationMillis = 400)
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 400)
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onSelected() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// ================== SURAH CARD ==================
@Composable
fun SurahCard(surah: Surah) {
    var pressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = if (pressed) 20.dp else 4.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0xFFBFA14A),
                spotColor = Color(0xFFBFA14A)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF0B1220),
                        Color(0xFF111827),
                        Color(0xFF0B1220)
                    )
                )
            )
            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(22.dp))
            .clickable { pressed = !pressed }
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IslamicNumberCircle(
                number = surah.number,
                icon = R.drawable.islamic_pattern
            )
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = surah.nameAr,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${surah.nameEn} • ${surah.ayahCount} آية",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            SurahTypeBadge(type = surah.type)
        }
    }
}
@Composable
fun IslamicNumberCircle(
    number: Int,
    icon: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(60.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .alpha(0.9f)
        )
        Text(
            text = number.toString(),
            color = Color(0xFFFFFFFF),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
@Composable
fun SurahTypeBadge(type: String) {

    val (bgColor, borderColor, textColor) = when (type) {
        "مكية" -> Triple(
            Color(0xFF22C55E).copy(alpha = 0.15f),
            Color(0xFF22C55E),
            Color(0xFF4ADE80)
        )

        "مدنية" -> Triple(
            Color(0xFF3B82F6).copy(alpha = 0.15f),
            Color(0xFF3B82F6),
            Color(0xFF60A5FA)
        )

        else -> Triple(
            Color.Gray.copy(alpha = 0.15f),
            Color.Gray,
            Color.White
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = type,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}


// ================== PREVIEW DATA ==================

val sampleSurahs = listOf(
    Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية"),
    Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية"),
    Surah(3, "آل عمران", "Ali 'Imran", 200, "مدنية"),
    Surah(4, "النساء", "An-Nisa", 176, "مدنية"),
    Surah(5, "المائدة", "Al-Ma'idah", 120, "مدنية")
)

// ================== PREVIEW ==================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SurahListPreview() {
    SurahListScreen(sampleSurahs)
}
@Preview(showBackground = true, backgroundColor = 0xFF020617)
@Composable
fun SurahCardPreview() {
    val mockSurah = Surah(
        number = 1,
        nameAr = "الفاتحة",
        nameEn = "Al-Fatihah",
        ayahCount = 7,
        type = "مكية"
    )

    Box(modifier = Modifier.padding(16.dp)) {
        SurahCard(surah = mockSurah)
    }
}

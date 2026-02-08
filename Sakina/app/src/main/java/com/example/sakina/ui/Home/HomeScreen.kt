package com.example.sakina.ui.Home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import com.example.sakina.R
import com.example.sakina.ui.Home.components.HomeCard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.sakina.ui.Home.components.HomeCard2
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LifecycleResumeEffect
val NeonCyan = Color(0xFF00FFD1)
val NeonPurple = Color(0xFFBD00FF)
val NeonGold = Color(0xFFFFD700)
val NeonRed =Color(0xFFFF7171)
val NeonGreen =Color(0xFF4DFF88)
val NeonPink =Color(0xFFFA2C71)





fun getAdjustedHijrahDate(adjustment: Long = 0): String {

    val hijrahDate = HijrahDate.now().plus(adjustment, ChronoUnit.DAYS)


    val months = arrayOf(
        "المحرّم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )


    val day = hijrahDate.get(ChronoField.DAY_OF_MONTH)
    val monthIndex = hijrahDate.get(ChronoField.MONTH_OF_YEAR) - 1 // نطرح 1 لأن المصفوفة تبدأ من 0
    val year = hijrahDate.get(ChronoField.YEAR)


    return "$day ${months[monthIndex]} $year"
}

@Composable
fun GalaxyBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = java.util.Random(42)
            repeat(100) {
                drawCircle(
                    color = Color.White.copy(alpha = random.nextFloat()),
                    radius = random.nextFloat() * 1.5.dp.toPx(),
                    center = Offset(
                        random.nextFloat() * size.width,
                        random.nextFloat() * size.height
                    )
                )
            }
        }
        content()
    }
}


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAzkarCardClick: () -> Unit = {},
    onDuaCardClick: () -> Unit = {},
    onQuranCardClick: () -> Unit = {},
    onNavigateToSurahDetails: (Int, String, Int) -> Unit,
    onTasbeehCardClick: () -> Unit = {},
    onCheckCardClick: () -> Unit = {},
    onSalahCardClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.userFlow.collectAsState(initial = null)
    val prayerCompleted by viewModel.prayerCompleted.collectAsState(initial = 0)
    val prayerTotal by viewModel.prayerTotal.collectAsState(initial = 5)
    val tasbeehCount by viewModel.tasbeehCount.collectAsState(initial = 0)
    val dailyDua by viewModel.dailyDua
    LaunchedEffect(Unit) {
        viewModel.refreshLastRead()
        viewModel.loadPrayerAndTasbeeh()
        viewModel.loadDailyDua()
    }
    LifecycleResumeEffect(Unit) {
        viewModel.loadPrayerAndTasbeeh()
        onPauseOrDispose { }
    }
    GalaxyBackground {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "أهلاً بك, ${user?.name ?: ""}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                shadow = Shadow(
                                    Color.White.copy(alpha = 0.5f),
                                    blurRadius = 15f
                                )
                            )
                        )

                        Text(
                            text = getAdjustedHijrahDate(),
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item {
                DuaCard(text = dailyDua?.text ?: "يا حي يا قيوم برحمتك أستغيث...")
            }
            item { HomeCard("صلاتي", "المغرب 6:32", NeonGold, R.drawable.pray,
                    trailingContent = {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(2.dp, NeonGold.copy(alpha = 0.5f), CircleShape)
                                .background(NeonGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("$prayerCompleted/$prayerTotal", color = NeonGold, fontWeight = FontWeight.Bold)
                        }
                    },
                onClick = {onSalahCardClick()})

            }
            item {
                HomeCard(
                    title = "القرءان الكريم",
                    subtitle = viewModel.subTitle(),
                    activeColor = NeonCyan,
                    imageRes =  R.drawable.koran,
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(45.dp)
                                .background(NeonPurple.copy(alpha = 0.3f), CircleShape)
                                .padding(8.dp)
                        )
                    },
                    onClick = { val id = viewModel.lastSurahId.value
                        val name = viewModel.lastSurahName.value
                        val count = viewModel.lastAyahCount.value

                        if (id != -1) {
                            onNavigateToSurahDetails(id, name, count)
                        } else {
                            onQuranCardClick()
                        }}
                )
            }
            item {
                    HomeCard("الأذكار", " ", NeonPurple, R.drawable.decoration,onClick = { onAzkarCardClick() })
                }
            item {
                HomeCard(
                    title = "جوامع الدعاء",
                    subtitle = "مختصر الكلم وطيب الأثر",
                    activeColor = NeonRed,
                    imageRes = R.drawable.islamic_pattern,
                    trailingContent = {
                    },
                    onClick = { onDuaCardClick() }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCard2(
                        title = "تسبيح",
                        subtitle = tasbeehCount.toString(),
                        activeColor = NeonGreen,
                        imageRes = R.drawable.tasbih,
                        modifier = Modifier.weight(1f),
                                onClick = { onTasbeehCardClick() }
                    )
                    HomeCard2(
                        title = "هعمل ايه النهاردة؟",
                        subtitle = "",
                        activeColor = NeonPink,
                        imageRes = R.drawable.arabic,
                        modifier = Modifier.weight(1f),
                                onClick = { onCheckCardClick() }
                    )
                            }
                }
            }
    }
}

@Composable
fun DuaCard(text: String) {
    val cyanColor = Color(0xFF2075B7)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(24.dp), spotColor = cyanColor)
            .background(cyanColor.copy(alpha = 0.35f), RoundedCornerShape(34.dp))
            .border(BorderStroke(4.dp, Brush.verticalGradient(listOf(cyanColor, Color.Transparent))), RoundedCornerShape(24.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "دعاء اليوم",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
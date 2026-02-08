package com.example.sakina.ui.Home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.sakina.ui.Home.components.HomeCard2
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.random.Random

val NeonCyan = Color(0xFF00FFD1)
val NeonPurple = Color(0xFFBD00FF)
val NeonGold = Color(0xFFFFD700)
val NeonRed =Color(0xFFFF7171)
val NeonGreen =Color(0xFF4DFF88)
val NeonPink =Color(0xFFFA2C71)




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
    LaunchedEffect(Unit) {
        viewModel.refreshLastRead()
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "صباح الخير,",
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
                        text = "9 رمضان 1447",
                        color = Color.Gray.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            item { DuaCard() }
            item { HomeCard("صلاتي", "المغرب 6:32", NeonGold, R.drawable.pray,
                    trailingContent = {

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(2.dp, NeonGold.copy(alpha = 0.5f), CircleShape)
                                .background(NeonGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("3/5", color = NeonGold, fontWeight = FontWeight.Bold)
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
                HomeCard("جوامع الدعاء", " ", NeonRed, R.drawable.islamic_pattern,onClick = { onDuaCardClick() })
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeCard2(
                        title = "تسبيح",
                        subtitle = "250",
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

@Preview(showBackground = true, heightDp = 1500)

@Composable
fun DuaCard() {
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
                text = " \"لا اله الا الله وحده لا شريك له ,له الملك وله الحمد وهو على كل شئ قدير\" ",
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
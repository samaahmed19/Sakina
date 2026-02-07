package com.example.sakina.ui.Home

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
import androidx.compose.ui.text.style.TextAlign


val NeonCyan = Color(0xFF00FFD1)
val NeonPurple = Color(0xFFBD00FF)
val NeonGold = Color(0xFFFFD700)
val NeonRed =Color(0xFFFF7171)
val NeonGreen =Color(0xFF4DFF88)
val NeonPink =Color(0xFFFA2C71)




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
    onTasbeehCardClick: () -> Unit = {},
    onCheckCardClick: () -> Unit = {},
    onSalahCardClick: () -> Unit = {}

) {


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
            item { HomeCard("القرءان الكريم", "اقرأ وردك", NeonCyan, R.drawable.koran,
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
                    onClick = {onQuranCardClick()})
             }
            item {
                    HomeCard("الأذكار", " ", NeonPink, R.drawable.helal,onClick = { onAzkarCardClick() })
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
                        subtitle = "Counter",
                        activeColor = NeonGreen,
                        imageRes = R.drawable.tasbih,
                        modifier = Modifier.weight(1f)
                            .clickable { onTasbeehCardClick()}
                    )
                    HomeCard2(
                        title = "Check",
                        subtitle = "",
                        activeColor = NeonPurple,
                        imageRes = R.drawable.arabic,
                        modifier = Modifier.weight(1f)
                            .clickable { onCheckCardClick() }
                    )
                            }
                }
            }
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
@Composable
fun DuaCard() {
    val cyanColor = Color(0xFF948B29)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp), spotColor = cyanColor)
            .background(cyanColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .border(BorderStroke(4.dp, Brush.verticalGradient(listOf(cyanColor, Color.Transparent))), RoundedCornerShape(24.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = " \"لا اله الا الله وحده لا شريك له ,له الملك وله الحمد وهو على كل شئ قدير\" ",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "دعاء اليوم",
                color = Color.White,
                fontSize = 19.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
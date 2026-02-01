package com.example.sakina.ui.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.lifecycle.viewmodel.compose.viewModel

val NeonCyan = Color(0xFF00FFD1)
val NeonPurple = Color(0xFFBD00FF)
val NeonGold = Color(0xFFFFD700)


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
    viewModel: HomeViewModel = viewModel()
) {
    val greeting by viewModel.greetingFlow.collectAsState(initial = "أهلًا")

    GalaxyBackground {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 20.dp)) {

                    Text(
                        text = greeting,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = TextStyle(
                            shadow = Shadow(
                                Color.White.copy(alpha = 0.5f),
                                blurRadius = 10f
                            )
                        )
                    )

                    Text(
                        text = "9 رمضان 1447",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            item { HomeCard("صلاتي", "المغرب 6:32", NeonCyan, R.drawable.splash) }
            item { HomeCard("أذكار الصباح", " ", NeonPurple, R.drawable.splash) }
            item { HomeCard("ورد القرآن", "صفحة 293", NeonGold, R.drawable.splash) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}

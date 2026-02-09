package com.example.sakina.ui.Tasbeeh

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class TasbeehOption(
    val id: String,
    val text: String,
    val color: Color
)

@Composable
fun StarryBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "serenity")
    val offsetAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val stars = remember { List(60) { Offset(Random.nextFloat(), Random.nextFloat()) } }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0E1A))) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { starOffset ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = 3f,
                    center = Offset(starOffset.x * size.width, starOffset.y * size.height)
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1E2644).copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(offsetAnim, size.height / 2),
                    radius = 800f
                ),
                radius = 800f,
                center = Offset(offsetAnim - 200f, size.height / 4)
            )
        }
    }
}

@Composable
fun TasbeehScreen(viewModel: TasbeehViewModel = hiltViewModel()) {
    val tasbeehOptions = remember {
        listOf(
            TasbeehOption("subhan", "سبحان الله", Color(0xFF7DDECC)),
            TasbeehOption("alhamdulillah", "الحمد لله", Color(0xFFC36BE1)),
            TasbeehOption("allahu", "الله أكبر", Color(0xFFEED973)),
            TasbeehOption("istighfar", "أستغفر الله", Color(0xFF77EFA0)),
            TasbeehOption("salawat", "اللهم صل على محمد", Color(0xFFFF6294))
        )
    }

    var uiSelectedOption by remember { mutableStateOf(tasbeehOptions[0]) }

    val INHALE_DURATION = 5500L
    val HOLD_DURATION = 2000L
    val EXHALE_DURATION = 6500L

    LaunchedEffect(viewModel.hasStartedByClick) {
        if (viewModel.hasStartedByClick) {
            while (true) {
                viewModel.updateBreathPhase(BreathPhase.INHALE)
                delay(INHALE_DURATION)
                viewModel.updateBreathPhase(BreathPhase.HOLD)
                delay(HOLD_DURATION)
                viewModel.updateBreathPhase(BreathPhase.EXHALE)
                delay(EXHALE_DURATION)
            }
        }
    }

    val animationDuration = when (viewModel.breathPhase) {
        BreathPhase.INHALE -> INHALE_DURATION.toInt()
        BreathPhase.HOLD -> HOLD_DURATION.toInt()
        else -> EXHALE_DURATION.toInt()
    }

    val scale by animateFloatAsState(
        targetValue = if (!viewModel.hasStartedByClick) 1.0f else when (viewModel.breathPhase) {
            BreathPhase.INHALE -> 1.15f
            BreathPhase.HOLD -> 1.15f
            BreathPhase.EXHALE -> 0.92f
        },
        animationSpec = if (viewModel.breathPhase == BreathPhase.HOLD || !viewModel.hasStartedByClick) snap()
        else tween(durationMillis = animationDuration, easing = LinearOutSlowInEasing),
        label = "breathing_scale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text("التسبيح", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!viewModel.hasStartedByClick) "اضغط على الدائرة لتبدأ الشهيق والذكر..."
                else when(viewModel.breathPhase) {
                    BreathPhase.INHALE -> "استشعر السكينة مع الشهيق وتأمل الذكر..."
                    BreathPhase.HOLD -> "تأمل في عظمة الخالق..."
                    else -> "أخرج كل ما يشغلك مع الزفير..."
                },
                fontSize = 14.sp, color = Color.White.copy(0.5f), textAlign = TextAlign.Center,
                modifier = Modifier.height(45.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tasbeehOptions.forEach { option ->
                    TasbeehButton(option, uiSelectedOption.id == option.id) {
                        uiSelectedOption = option
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
                if (viewModel.hasStartedByClick) {
                    repeat(3) { index ->
                        OuterRing(viewModel.breathPhase, uiSelectedOption.color, index * 400L, animationDuration)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(scale)
                        .shadow(30.dp, CircleShape, ambientColor = uiSelectedOption.color)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(uiSelectedOption.color.copy(0.35f), Color(0xFF1A1F33).copy(0.9f))
                            ),
                            shape = CircleShape
                        )
                        .border(1.dp, uiSelectedOption.color.copy(0.4f), CircleShape)
                        .clickable {
                            viewModel.incrementCount()
                            if (!viewModel.hasStartedByClick) viewModel.hasStartedByClick = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (viewModel.hasStartedByClick) {
                            Text(
                                text = when(viewModel.breathPhase) {
                                    BreathPhase.INHALE -> "شهيق"
                                    BreathPhase.HOLD -> "ثبات"
                                    else -> "زفير"
                                },
                                fontSize = 12.sp, color = Color.White.copy(0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = viewModel.count.toString(),
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraLight,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))



            Spacer(modifier = Modifier.weight(1.5f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { viewModel.resetCount() },
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White.copy(0.04f), CircleShape)
                    .border(0.5.dp, Color.White.copy(0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Refresh, "Reset", tint = Color.White.copy(0.5f), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("إعادة التعيين", fontSize = 10.sp, color = Color.White.copy(0.2f))
        }
    }
}

@Composable
fun TasbeehButton(option: TasbeehOption, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) option.color.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.02f)
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (isSelected) option.color.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.08f)
        )
    ) {
        Text(
            option.text,
            fontSize = 16.sp,
            color = if (isSelected) option.color else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
fun OuterRing(breathPhase: BreathPhase, color: Color, delay: Long, animationDuration: Int) {
    val targetScale = if (breathPhase == BreathPhase.EXHALE) 1.35f else 1.0f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = animationDuration, delayMillis = delay.toInt(), easing = LinearEasing),
        label = ""
    )
    Box(
        modifier = Modifier
            .size(230.dp)
            .scale(scale)
            .border(1.dp, color.copy(alpha = 0.06f), CircleShape)
    )
}
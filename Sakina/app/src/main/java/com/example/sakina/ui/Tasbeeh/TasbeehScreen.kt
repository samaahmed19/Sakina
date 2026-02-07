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

// الـ TasbeehOption هنسيبه عشان الألوان اللي انت اخترتها في الـ UI
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
    // استخدمنا الـ hiltViewModel عشان نمنع الـ Crash

    // قائمة الخيارات اللي انت صممتها بألوانها
    val tasbeehOptions = remember {
        listOf(
            TasbeehOption("subhan", "سبحان الله", Color(0xFF7DDECC)),
            TasbeehOption("alhamdulillah", "الحمد لله", Color(0xFFC36BE1)),
            TasbeehOption("allahu", "الله أكبر", Color(0xFFEED973)),
            TasbeehOption("istighfar", "أستغفر الله", Color(0xFF77EFA0)),
            TasbeehOption("salawat", "اللهم صل على محمد", Color(0xFFFF6294))
        )
    }

    // ربط الاختيار الافتراضي من القائمة اللي فوق مع الـ ViewModel
    var uiSelectedOption by remember { mutableStateOf(tasbeehOptions[0]) }

    val INHALE_DURATION = 5500L
    val HOLD_DURATION = 2000L
    val EXHALE_DURATION = 6500L

    // التحكم في مراحل التنفس من خلال الـ ViewModel
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
            Spacer(modifier = Modifier.height(30.dp))

            Text("التسبيح", fontSize = 30.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (!viewModel.hasStartedByClick) "اضغط على الدائرة لتبدأ الشهيق والذكر..."
                else when(viewModel.breathPhase) {
                    BreathPhase.INHALE -> "استشعر السكينة مع الشهيق وتأمل الذكر..."
                    BreathPhase.HOLD -> "تأمل في عظمة الخالق..."
                    else -> "أخرج كل ما يشغلك مع الزفير..."
                },
                fontSize = 15.sp, color = Color.White.copy(0.6f), textAlign = TextAlign.Center,
                modifier = Modifier.height(50.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasbeehOptions.forEach { option ->
                    TasbeehButton(option, uiSelectedOption.id == option.id) {
                        uiSelectedOption = option
                        // لو عندك Entity في الـ ViewModel بنفس الاسم ممكن تربطهم هنا
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
                if (viewModel.hasStartedByClick) {
                    repeat(3) { index ->
                        OuterRing(viewModel.breathPhase, uiSelectedOption.color, index * 400L, animationDuration)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scale)
                        .shadow(25.dp, CircleShape, ambientColor = uiSelectedOption.color)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(uiSelectedOption.color.copy(0.4f), Color(0xFF1A1F33).copy(0.95f))
                            ),
                            shape = CircleShape
                        )
                        .border(1.5.dp, uiSelectedOption.color.copy(0.5f), CircleShape)
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
                                fontSize = 14.sp, color = Color.White.copy(0.5f)
                            )
                        }
                        Text(text = viewModel.count.toString(), fontSize = 60.sp, fontWeight = FontWeight.Light, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = uiSelectedOption.text,
                fontSize = 32.sp, fontWeight = FontWeight.Bold, color = uiSelectedOption.color,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { viewModel.resetCount() },
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White.copy(0.05f), CircleShape)
                    .border(1.dp, Color.White.copy(0.15f), CircleShape)
            ) {
                Icon(Icons.Default.Refresh, "Reset", tint = Color.White.copy(0.7f))
            }
            Text("إعادة التعيين", fontSize = 11.sp, color = Color.White.copy(0.3f))
        }
    }
}

@Composable
fun TasbeehButton(option: TasbeehOption, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) option.color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f)
        ),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) option.color.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Text(option.text, fontSize = 18.sp, color = if (isSelected) option.color else Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun OuterRing(breathPhase: BreathPhase, color: Color, delay: Long, animationDuration: Int) {
    val targetScale = if (breathPhase == BreathPhase.EXHALE) 1.4f else 1.0f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = animationDuration, delayMillis = delay.toInt(), easing = LinearEasing),
        label = ""
    )
    Box(
        modifier = Modifier
            .size(240.dp)
            .scale(scale)
            .border(1.dp, color.copy(alpha = 0.08f), CircleShape)
    )
}
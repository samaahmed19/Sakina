package com.sama.sakina.ui.Gwame3Dua.Favorite

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.sama.sakina.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Random

@Composable
fun DuaFavoriteScreen(
    viewModel: DuaFavoriteViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onDuaClick: (Int, Int) -> Unit
) {
    val favoriteDuas by viewModel.favorites.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
        label = "starAlpha"
    )
    val starMovement by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(80000, easing = LinearEasing), RepeatMode.Restart),
        label = "starMovement"
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A))))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val random = Random(42)
                repeat(150) {
                    val xPos = (random.nextFloat() * size.width + starMovement) % size.width
                    val yPos = (random.nextFloat() * size.height + starMovement * 0.2f) % size.height
                    drawCircle(
                        color = Color.White.copy(alpha = random.nextFloat() * starAlpha),
                        radius = (0.5.dp + (random.nextFloat().dp)).toPx(),
                        center = Offset(xPos, yPos)
                    )
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_forward_24),
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "أدعيتي المفضلة",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFD700),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (favoriteDuas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لم تقم بإضافة أي أدعية للمفضلة بعد", color = Color.White.copy(0.5f))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(favoriteDuas, key = { it.id }) { dua ->
                            DuaCardItem(
                                duaText = dua.text,
                                isFavorite = true,
                                onFavoriteClick = { viewModel.toggleFavorite(dua.id, true) },
                                onClick = {
                                    onDuaClick(dua.categoryId, dua.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DuaCardItem(
    duaText: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = duaText,
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(id = if (isFavorite) R.drawable.heart_filled else R.drawable.heart_outline),
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun StarCanvas() {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse), label = "alpha"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val random = Random(42)
        repeat(150) {
            drawCircle(
                color = Color.White.copy(alpha = random.nextFloat() * starAlpha),
                radius = (0.5.dp + (random.nextFloat().dp)).toPx(),
                center = Offset(random.nextFloat() * size.width, random.nextFloat() * size.height)
            )
        }
    }
}
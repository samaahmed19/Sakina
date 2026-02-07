package com.example.sakina.ui.Home.components
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    activeColor: Color,
    imageRes: Int,
    trailingContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    var isPressed by remember { mutableStateOf(false) }


    val neonAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 0.0f,
        animationSpec = tween(durationMillis = 400), label = ""
    )

    val borderThickness by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { isPressed = !isPressed
                onClick()}

            .shadow(
                elevation = if (isPressed) 30.dp else 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = activeColor.copy(alpha = neonAlpha),
                ambientColor = activeColor.copy(alpha = neonAlpha)
            )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPressed) activeColor.copy(alpha = 0.02f) else activeColor.copy(alpha = 0.03f),
            ),
            border = BorderStroke(
                width = borderThickness,
                brush = Brush.linearGradient(
                    colors = if (isPressed) listOf(activeColor, activeColor.copy(alpha = 0.5f))
                    else listOf(activeColor, activeColor.copy(alpha = 0.2f), )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .drawBehind {
                            if (isPressed) {
                                drawCircle(
                                    color = activeColor,
                                    style = Stroke(width = 3.dp.toPx()),
                                    alpha = neonAlpha
                                )
                            }
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = activeColor.copy(alpha = neonAlpha),
                                blurRadius = 15f
                            )
                        )
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }


                Box(
                    modifier = Modifier.padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingContent()
                }
            }
        }
    }
    }
}
@Composable
fun HomeCard2(
    title: String,
    subtitle: String,
    activeColor: Color,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    var isPressed by remember { mutableStateOf(false) }


    val neonAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 0.0f,
        animationSpec = tween(durationMillis = 400), label = ""
    )

    val borderThickness by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { isPressed = !isPressed
                onClick()}

            .shadow(
                elevation = if (isPressed) 30.dp else 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = activeColor.copy(alpha = neonAlpha),
                ambientColor = activeColor.copy(alpha = neonAlpha)
            )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPressed) activeColor.copy(alpha = 0.02f) else activeColor.copy(alpha = 0.04f),
            ),
            border = BorderStroke(
                width = borderThickness,
                brush = Brush.linearGradient(
                    colors = if (isPressed) listOf(activeColor, activeColor.copy(alpha = 0.5f))
                    else listOf(activeColor, activeColor.copy(alpha = 0.2f), )
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .drawBehind {
                            if (isPressed) {
                                drawCircle(
                                    color = activeColor,
                                    style = Stroke(width = 2.dp.toPx()),
                                    alpha = neonAlpha
                                )
                            }
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))


                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(
                                color = activeColor.copy(alpha = neonAlpha),
                                blurRadius = 15f
                            )
                        )
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 19.sp
                    )

            }
        }
    }
}
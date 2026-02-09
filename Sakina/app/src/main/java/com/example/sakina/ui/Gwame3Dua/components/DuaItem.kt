package com.example.sakina.ui.Gwame3Dua.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sakina.R

@Composable
fun DuaItem(
    duaText: String,
    isFavorite: Boolean,
    activeColor: Color,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(activeColor.copy(alpha = 0.4f), Color.Transparent)))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.star),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = activeColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = duaText, color = Color.White, fontSize = 20.sp,
                fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                lineHeight = 36.sp, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.heart_outline),
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
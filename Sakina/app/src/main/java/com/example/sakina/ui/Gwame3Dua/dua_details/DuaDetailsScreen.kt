package com.example.sakina.ui.Gwame3Dua.dua_details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakina.R

@Composable
fun DuaDetailsScreen(
    viewModel: DuaDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val duas by viewModel.duas.collectAsState()
    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A))))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(painterResource(id = R.drawable.arrow_forward_24), "Back", tint = Color.White)
                    }

                    Text(
                        text = viewModel.categoryTitle,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFD700),
                        fontSize = 24.sp, // خط العنوان كبير
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(duas) { dua ->
                        DuaCardItem(
                            duaText = dua.text,
                            isFavorite = dua.isFavorite,
                            onFavoriteClick = { viewModel.toggleFavorite(dua.id, dua.isFavorite) },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Dua", dua.text))
                                Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, dua.text)
                                }
                                context.startActivity(Intent.createChooser(intent, "مشاركة عبر:"))
                            }
                        )
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
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = duaText,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Divider(color = Color.White.copy(alpha = 0.05f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onCopy) {
                    Icon(painterResource(id = R.drawable.copy), "Copy", tint = Color.White.copy(0.6f), modifier = Modifier.size(24.dp))
                }

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        painter = painterResource(id = if (isFavorite) R.drawable.favorite else R.drawable.favorite),
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = onShare) {
                    Icon(painterResource(id = R.drawable.share), "Share", tint = Color.White.copy(0.6f), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
package com.example.sakina.ui.Azkar.azkar_details


import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ZikrTopAppBar(title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "رجوع",
                    tint = Color.White
                )
            }
            Text(
                text = title,
                fontSize = 26.sp,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 48.dp)
        )
    }
}
@Composable
fun TotalProgressHeader(completedCount: Int, totalAzkarCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2B)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFF252B3B))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "$completedCount / $totalAzkarCount",
                    color = Color(0xFFFFC107),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "التقدم الكلي",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            LinearProgressIndicator(
                progress = if (totalAzkarCount > 0) completedCount.toFloat() / totalAzkarCount else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFFC107),
                trackColor = Color(0xFF252B3B)
            )
        }
    }
}

@Composable
fun ZikrDetailsScreen(
    categoryId: String,
    viewModel: AzkarViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(categoryId) {
        viewModel.loadAzkar(categoryId)
    }

    val azkarList = viewModel.azkarList
    val completedCount = azkarList.count { it.currentCount >= it.maxCount }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Color(0xFF0A0E14),
            topBar = {

                ZikrTopAppBar(
                    title = viewModel.categoryTitle,
                    onBackClick = onBackClick
                )
            }
        ) { padding ->
            if (azkarList.isEmpty()) {
                // حالة احتياطية لو البيانات لسه بتتحمل
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFC107))
                }
            } else {
                LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                    item { TotalProgressHeader(completedCount, azkarList.size) }

                    items(
                        items = azkarList,
                        key = { it.id }
                    ) { zikr ->
                        ZikrCard(
                            zikr = zikr,
                            onCounterClick = { viewModel.incrementCount(zikr.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZikrCard(zikr: ZikrItemState, onCounterClick: () -> Unit) {
    val isCompleted = zikr.currentCount >= zikr.maxCount
    val backgroundColor = if (isCompleted) Color(0xFF0F3D3E) else Color(0xFF1A1F2B)

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        border = if (isCompleted) BorderStroke(1.dp, Color(0xFF23C4B6)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(zikr.text, fontSize = 20.sp, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())


            if (zikr.reward.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252B3B), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("الفضل / المعنى:", color = Color(0xFF23C4B6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(zikr.reward, color = Color.White, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("التكرار", color = Color.Gray)
                Text("${zikr.currentCount} / ${zikr.maxCount}", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
            }

            LinearProgressIndicator(
                progress = { if (zikr.maxCount > 0) zikr.currentCount.toFloat() / zikr.maxCount else 0f },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(6.dp).clip(CircleShape),
                color = if (isCompleted) Color(0xFF23C4B6) else Color(0xFFFFC107),
                trackColor = Color.DarkGray
            )

            if (isCompleted) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                    Text("تم الإكمال", color = Color(0xFF23C4B6))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF23C4B6), modifier = Modifier.size(18.dp).padding(start = 4.dp))
                }
            } else {
                Button(
                    onClick = onCounterClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("قرأت هذا الذكر", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
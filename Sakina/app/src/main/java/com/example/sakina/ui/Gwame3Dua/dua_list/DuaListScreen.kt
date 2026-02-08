package com.example.sakina.ui.Gwame3Dua

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sakina.R
import com.example.sakina.data.local.database.entity.DuaCategoryEntity
import com.example.sakina.ui.Gwame3Dua.dua_list.DuaViewModel
import java.util.Random

@Composable
fun DuaSmartNeonCard(
    title: String,
    activeColor: Color,
    duaCount: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedAlpha by animateFloatAsState(targetValue = if (isPressed) 0.22f else 0.06f, label = "alpha")
    val animatedGlow by animateDpAsState(targetValue = if (isPressed) 22.dp else 0.dp, label = "glow")

    val iconScale by animateFloatAsState(targetValue = if (isPressed) 1.1f else 1.0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(
                elevation = animatedGlow,
                shape = RoundedCornerShape(24.dp),
                spotColor = activeColor,
                ambientColor = Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isPressed = !isPressed
                onClick()
            }
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = if (isPressed) listOf(activeColor, activeColor.copy(alpha = 0.2f))
                    else listOf(activeColor.copy(alpha = 0.5f), Color.Transparent)
                )
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                activeColor.copy(alpha = animatedAlpha),
                                activeColor.copy(alpha = 0.02f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(85.dp)
                                .graphicsLayer(
                                    scaleX = iconScale,
                                    scaleY = iconScale,
                                    shadowElevation = if(isPressed) 20f else 0f
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(horizontal = 4.dp),
                        lineHeight = 22.sp
                    )

                    Surface(
                        color = activeColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(0.5.dp, activeColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "$duaCount دعاء",
                            color = activeColor.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DuaListScreen(
    viewModel: DuaViewModel = hiltViewModel(),
    onCategoryClick: (DuaCategoryEntity) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    DuaListContent(
        categories = categories,
        onFilterSelected = { viewModel.onFilterChanged(it) },
        onCategoryClick = onCategoryClick
    )
}

@Composable
fun DuaListContent(
    categories: List<DuaCategoryEntity>,
    onFilterSelected: (String) -> Unit,
    onCategoryClick: (DuaCategoryEntity) -> Unit
) {
    val softNeonColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFBD00FF),
        Color(0xFF00FFD1),
        Color(0xFFFF4D4D),
        Color(0xFF4DFF88),
        Color(0xFF00E5FF),
        Color(0xFFFF007F),
        Color(0xFFFF8C00),
        Color(0xFFCCFF00),
        Color(0xFF007BFF)
    )

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
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A)))
            )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val random = Random(42)
                repeat(200) {
                    val xPos = (random.nextFloat() * size.width + starMovement) % size.width
                    val yPos = (random.nextFloat() * size.height + starMovement * 0.3f) % size.height
                    drawCircle(
                        color = Color.White.copy(alpha = random.nextFloat() * starAlpha),
                        radius = (0.5.dp + (random.nextFloat().dp)).toPx(),
                        center = Offset(xPos, yPos)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 35.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("جوامع الدعاء", color = Color(0xFFFFD700), fontSize = 40.sp, fontWeight = FontWeight.Black)
                        Text("ادْعُ اللَّهَ بِمَا شِئْتَ مِنْ خَيْرَيِ الدُّنْيَا وَالْآخِرَةِ", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                    }
                }
                item(span = { GridItemSpan(2) }) {
                    DuaAyahCard()
                }
                item(span = { GridItemSpan(2) }) { DuaFiltersBar(onFilterSelected = onFilterSelected) }

                itemsIndexed(categories) { index, category ->
                    DuaSmartNeonCard(
                        title = category.title,
                        activeColor = softNeonColors[index % softNeonColors.size],
                        imageRes = getDuaIconResource(category.icon),
                        duaCount = category.count.toString(),
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}
@Composable
fun getDuaIconResource(iconName: String?): Int {
    return when (iconName) {
        "repentance" -> R.drawable.repentance
        "Anbya"      -> R.drawable.star
        "guidance"   -> R.drawable.guidance
        "protection" -> R.drawable.protection
        "rizk"       -> R.drawable.rizk
        "Quran"      -> R.drawable.star
        "book"       -> R.drawable.book
        "light"      -> R.drawable.light
        "star"       -> R.drawable.star
        "health"     -> R.drawable.health
        "hereafter"  -> R.drawable.hereafter
        "praise"     -> R.drawable.praise
        else         -> R.drawable.splash
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaFiltersBar(onFilterSelected: (String) -> Unit) {
    val filters = listOf("الكل", "المغفرة" , "دعاء الأنبياء" , "الهداية", "الاستعاذة","أدعية القرآن" ,"الرزق", "العلم", "الطمأنينة", "جوامع", "العافية", "الآخرة", "الثناء")
    var selectedFilter by remember { mutableStateOf("الكل") }
    LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
                selected = isSelected,
                onClick = { selectedFilter = filter; onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)) },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f)
                ),
                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected,
                    borderColor = Color.White.copy(alpha = 0.2f),
                    selectedBorderColor = Color(0xFF00E5FF),
                    borderWidth = 1.dp, selectedBorderWidth = 1.5.dp)
            )
        }
    }
}


@Composable
fun DuaAyahCard() {
    val cyanColor = Color(0x8500CCFF)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .shadow(elevation = 0.dp, shape = RoundedCornerShape(24.dp), spotColor = cyanColor)
            .background(cyanColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .border(BorderStroke(4.dp, Brush.verticalGradient(listOf(cyanColor, Color.Transparent))), RoundedCornerShape(24.dp))
            .padding(7.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = " \"﴿ وَقَالَ رَبُّكُمُ ادْعُونِي أَسْتَجِبْ لَكُم ﴾\" ",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "سورة غافر - آية 60",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

//@Preview(showBackground = true, device = "id:pixel_7")
//@Composable
//fun DuaListPreview() {
//    val sampleCategories = listOf(
//        DuaCategoryEntity("1", "أدعية التوبة", "maghfirah", 12),
//        DuaCategoryEntity("2", "أدعية الرزق", "rizq", 8),
//        DuaCategoryEntity("3", "أدعية السكينة", "tranquility", 5),
//        DuaCategoryEntity("4", "أدعية الهداية", "guidance", 10),
//        DuaCategoryEntity("5", "أدعية الشفاء", "wellness", 15),
//        DuaCategoryEntity("6", "أدعية العلم", "knowledge", 7),
//        DuaCategoryEntity("7", "أدعية الحماية", "protection", 9),
//        DuaCategoryEntity("8", "جوامع الدعاء", "comprehensive", 20),
//        DuaCategoryEntity("9", "أدعية الآخرة", "hereafter", 6),
//        DuaCategoryEntity("10", "أدعية الثناء", "praise", 11)
//    )
//    DuaListContent(categories = sampleCategories, onFilterSelected = {}, onCategoryClick = {})
//}
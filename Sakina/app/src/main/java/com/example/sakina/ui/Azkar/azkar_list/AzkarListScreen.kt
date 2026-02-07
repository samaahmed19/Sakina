package com.example.sakina.ui.Azkar.azkar_list

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import com.example.sakina.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SmartNeonCard(
    title: String,
    subtitle: String,
    tag: String,
    activeColor: Color,
    imageRes: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }


    val animatedAlpha by animateFloatAsState(targetValue = if (isPressed) 0.3f else 0.06f, label = "alpha")
    val animatedGlow by animateDpAsState(targetValue = if (isPressed) 40.dp else 0.dp, label = "glow")


    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = if (isPressed) listOf(activeColor, activeColor.copy(alpha = 0.2f))
                        else listOf(activeColor.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    activeColor.copy(alpha = animatedAlpha),
                                    activeColor.copy(alpha = 0.03f)
                                )
                            )
                        )
                        .padding(30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .background(activeColor.copy(alpha = 0.1f), CircleShape)
                            .drawBehind {
                                drawCircle(
                                    color = activeColor,
                                    style = Stroke(width = 2.dp.toPx()),
                                    alpha = if (isPressed) 1f else 0.6f
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Label
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }

                    //Tag
                    Surface(
                        color = activeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(0.5.dp, activeColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = activeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                }
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AzkarListScreen(viewModel: AzkarViewModel = hiltViewModel() ,
                        onCategoryClick: (String) -> Unit) {
    val neonColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFBD00FF),
        Color(0xFF00FFD1),
        Color(0xFFFF4D4D),
        Color(0xFF4DFF88)
    )
        val categories by viewModel.categories.collectAsState()

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Galaxy
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF020617),
                                Color(0xFF0F172A)
                            )
                        )
                    )
            ) {
                // Stars
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val random = java.util.Random(42)
                    repeat(200) { //Num
                        val x = random.nextFloat() * size.width
                        val y = random.nextFloat() * size.height
                        val alpha = random.nextFloat()
                        val radius = random.nextFloat() * 2.dp.toPx()


                        if (random.nextBoolean()) {
                            // little
                            drawCircle(
                                color = Color.White.copy(alpha = alpha),
                                radius = radius * 0.5f,
                                center = Offset(x, y)
                            )
                        } else {
                            // shiny
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.White.copy(alpha = alpha), Color.Transparent),
                                    center = Offset(x, y),
                                    radius = radius * 2f
                                ),
                                radius = radius * 2f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 35.dp, bottom = 10.dp)
                ) {
                    item {
                        Text(
                            text = "الأذكار",
                            color = Color(0xFFFFD700), 
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "لِمَنْ أَرَادَ أَنْ يَذَّكَّرَ أَوْ أَرَادَ شُكُورًا",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(bottom = 32.dp)
                                .fillMaxWidth()
                        )
                    }
                    item { AyahCard() }
                    item {

                        AzkarFiltersBar(
                            selectedFilter = viewModel.selectedFilter.value,
                            onFilterSelected = { selected ->
                                viewModel.onFilterTextChanged(selected)
                            }
                        )
                    }
                    // Samples
                    itemsIndexed(categories) { index, category ->
                        // Random color
                        val cardColor = neonColors[index % neonColors.size]

                        SmartNeonCard(
                            title = category.title,
                            subtitle = " ",
                            tag = "استكشف",
                            activeColor = cardColor,
                            imageRes = getIconResource(category.icon),
                           onClick = { onCategoryClick(category.id) }
                        )


                    }}}
            }
        }



@Composable
fun getIconResource(iconName: String): Int {
    return when (iconName) {
        "sun" -> R.drawable.sun
        "moon" -> R.drawable.moon
        "bed" -> R.drawable.bed
        "carpet" -> R.drawable.prayerrug
        "wakeup"-> R.drawable.morning
        "carpet2"-> R.drawable.prayer
        "mosque"-> R.drawable.mosque
        "azan"-> R.drawable.prophet
         "water"-> R.drawable.wudu
         "home"-> R.drawable.house
        "food"-> R.drawable.cutlery
        else -> R.drawable.arabic
    }
}



@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AzkarFiltersBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf("الكل", "الصباح", "المساء", "النوم", "الصلاة")


    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selectedFilter == filter

            FilterChip(
                selected = isSelected,
                onClick = {

                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = filter,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                },
                shape = RoundedCornerShape(50.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    selectedContainerColor = Color(0xFF00E5FF).copy(alpha = 0.2f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.White.copy(alpha = 0.2f),
                    selectedBorderColor = Color(0xFF00E5FF),
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.5.dp
                ),
                elevation = if (isSelected) {
                    FilterChipDefaults.filterChipElevation(elevation = 8.dp)
                } else null
            )
        }
    }
}
@Composable
fun AyahCard() {
    val cyanColor = Color(0x8500CCFF)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(24.dp), spotColor = cyanColor)
            .background(cyanColor.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .border(BorderStroke(4.dp, Brush.verticalGradient(listOf(cyanColor, Color.Transparent))), RoundedCornerShape(24.dp))
            .padding(7.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.splash),
                contentDescription = null,
                tint = cyanColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = " \"﴿ فَاذْكُرُونِي أَذْكُرْكُمْ وَاشْكُرُوا لِي وَلَا تَكْفُرُونِ ﴾\" ",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "سورة البقرة - آية 152",
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
    }

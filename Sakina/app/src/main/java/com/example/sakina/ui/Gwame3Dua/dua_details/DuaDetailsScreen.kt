package com.example.sakina.ui.Gwame3Dua.dua_details

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.sakina.R
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DuaDetailsScreen(
    viewModel: DuaDetailsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val duas by viewModel.duas.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A))))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(painterResource(id = R.drawable.arrow_forward_24), "رجوع", tint = Color.White)
                    }
                    Text(
                        text = viewModel.categoryTitle,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFD700),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    items(duas, key = { it.id }) { dua ->
                        DuaCardItem(
                            duaText = dua.text,
                            isFavorite = dua.isFavorite,
                            onFavoriteClick = { viewModel.toggleFavorite(dua.id, dua.isFavorite) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun DuaCardItem(
    duaText: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("dua_prefs", Context.MODE_PRIVATE) }
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()
    val duaKey = remember(duaText) { duaText.hashCode().toString() }

    val parts = remember(duaText) {
        val lastOpenBracket = duaText.lastIndexOf('(')
        val lastCloseBracket = duaText.lastIndexOf(')')
        if (lastOpenBracket != -1 && lastCloseBracket != -1 && lastCloseBracket > lastOpenBracket) {
            val mainText = duaText.substring(0, lastOpenBracket).trim()
            val source = duaText.substring(lastOpenBracket + 1, lastCloseBracket).replace("_", " ").trim()
            Pair(mainText, source)
        } else { Pair(duaText, null) }
    }

    val patterns = remember { listOf(R.drawable.pattern_1, R.drawable.pattern_2, R.drawable.pattern_3, R.drawable.pattern_4, R.drawable.pattern_5, R.drawable.pattern_6, R.drawable.pattern_7, R.drawable.pattern_8) }
    val colors = remember { listOf(Color(0xFF0F172A), Color(0xFF1E1E2C), Color(0xFF2D1B36), Color(0xFF1A2E1A), Color(0xFF2C1616), Color(0xFF000000)) }

    val userAddedImages = remember {
        val savedSet = prefs.getStringSet("added_images", emptySet()) ?: emptySet()
        mutableStateListOf<Uri>().apply { addAll(savedSet.map { Uri.parse(it) }) }
    }

    var currentImageUri by remember { mutableStateOf(prefs.getString("uri_$duaKey", null)?.let { Uri.parse(it) }) }

    LaunchedEffect(userAddedImages.size) {
        if (currentImageUri != null && !userAddedImages.contains(currentImageUri)) {
            currentImageUri = null
        }
    }

    var selectedPattern by remember { mutableIntStateOf(prefs.getInt("pattern_$duaKey", patterns[0])) }
    var selectedColor by remember { mutableStateOf(Color(prefs.getInt("color_$duaKey", colors[0].toArgb()))) }
    var patternAlpha by remember { mutableFloatStateOf(prefs.getFloat("alpha_$duaKey", 0.15f)) }
    var imageToDelete by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            if (!userAddedImages.contains(it)) {
                userAddedImages.add(it)
                scope.launch(Dispatchers.IO) { prefs.edit().putStringSet("added_images", userAddedImages.map { it.toString() }.toSet()).apply() }
            }
            currentImageUri = it
            scope.launch(Dispatchers.IO) { prefs.edit().putString("uri_$duaKey", it.toString()).apply() }
        }
    }

    if (imageToDelete != null) {
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            title = { Text("حذف الصورة") },
            text = { Text("سيتم حذف هذه الصورة من قائمة الخلفيات في جميع الكروت.") },
            confirmButton = {
                TextButton(onClick = {
                    val uriToRemove = imageToDelete!!
                    userAddedImages.remove(uriToRemove)
                    scope.launch(Dispatchers.IO) {
                        val editor = prefs.edit()

                        editor.putStringSet("added_images", userAddedImages.map { it.toString() }.toSet())

                        val allKeys = prefs.all
                        allKeys.forEach { (key, value) ->
                            if (key.startsWith("uri_") && value == uriToRemove.toString()) {
                                editor.remove(key)
                            }
                        }
                        editor.apply()
                    }
                    imageToDelete = null
                }) { Text("حذف", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { imageToDelete = null }) { Text("إلغاء") } }
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().capturable(captureController)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700).copy(0.2f)),
                colors = CardDefaults.cardColors(containerColor = selectedColor)
            ) {
                Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    Image(
                        painter = rememberAsyncImagePainter(currentImageUri ?: selectedPattern),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        alpha = patternAlpha
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = parts.first, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 40.sp)
                        parts.second?.let { source ->
                            Spacer(modifier = Modifier.height(22.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(24.dp, 1.dp).background(Color(0xFFFFD700).copy(0.4f)))
                                Text(text = source, color = Color(0xFFFFD700), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 12.dp))
                                Box(modifier = Modifier.size(24.dp, 1.dp).background(Color(0xFFFFD700).copy(0.4f)))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
            Text("تنسيق الخلفية", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                item {
                    Box(modifier = Modifier.size(55.dp).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(0.05f)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(15.dp)).clickable { photoPickerLauncher.launch("image/*") }, contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.image_gallery), null, tint = Color(0xFFFFD700), modifier = Modifier.size(26.dp))
                    }
                }
                items(userAddedImages, key = { it.toString() }) { uri ->
                    Box(modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(2.dp, if (currentImageUri == uri) Color(0xFFFFD700) else Color.Transparent, RoundedCornerShape(15.dp))
                        .combinedClickable(
                            onClick = {
                                currentImageUri = uri
                                scope.launch(Dispatchers.IO) { prefs.edit().putString("uri_$duaKey", uri.toString()).apply() }
                            },
                            onLongClick = { imageToDelete = uri }
                        )
                    ) {
                        Image(painter = rememberAsyncImagePainter(uri), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                }
                items(patterns) { pattern ->
                    Box(modifier = Modifier.size(55.dp).clip(RoundedCornerShape(15.dp)).border(2.dp, if (selectedPattern == pattern && currentImageUri == null) Color(0xFFFFD700) else Color.Transparent, RoundedCornerShape(15.dp)).clickable {
                        selectedPattern = pattern
                        currentImageUri = null
                        scope.launch(Dispatchers.IO) { prefs.edit().putInt("pattern_$duaKey", pattern).remove("uri_$duaKey").apply() }
                    }) {
                        Image(painterResource(pattern), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(), alpha = 0.6f)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(colors) { color ->
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color).border(if (selectedColor == color) 2.dp else 0.dp, Color.White, CircleShape).clickable {
                        selectedColor = color
                        scope.launch(Dispatchers.IO) { prefs.edit().putInt("color_$duaKey", color.toArgb()).apply() }
                    })
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(painterResource(R.drawable.visibility), null, tint = Color.White.copy(0.5f), modifier = Modifier.size(20.dp))
                Slider(
                    value = patternAlpha,
                    onValueChange = { patternAlpha = it },
                    onValueChangeFinished = { scope.launch(Dispatchers.IO) { prefs.edit().putFloat("alpha_$duaKey", patternAlpha).apply() } },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFFD700), activeTrackColor = Color(0xFFFFD700), inactiveTrackColor = Color.White.copy(0.1f)),
                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
                )
                Text(
                    text = "${(patternAlpha * 100).toInt()}%",
                    color = Color.White.copy(0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionLargeBtn(R.drawable.copy, "نسخ", Color(0xFF4FC3F7)) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Dua", parts.first))
                Toast.makeText(context, "تم نسخ الدعاء", Toast.LENGTH_SHORT).show()
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onFavoriteClick() }) {
                Box(
                    modifier = Modifier.size(55.dp).background(Color.White.copy(0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painterResource(if (isFavorite) R.drawable.heart_filled else R.drawable.heart_outline), null, tint = if (isFavorite) Color.Red else Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("تفضيل", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }

            ActionLargeBtn(R.drawable.download, "حفظ", Color(0xFF00FFD1)) {
                scope.launch {
                    try {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        saveBitmapToGallery(context, bitmap)
                    } catch (e: Exception) { Toast.makeText(context, "فشل الحفظ", Toast.LENGTH_SHORT).show() }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp, color = Color.White.copy(0.1f))
    }
}

@Composable
fun ActionLargeBtn(icon: Int, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(55.dp).background(Color.White.copy(0.05f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(icon), null, tint = color, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = Color.White.copy(0.6f), fontSize = 12.sp)
    }
}

fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
    val filename = "Sakina_${System.currentTimeMillis()}.jpg"
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Sakina")
    }
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    uri?.let {
        context.contentResolver.openOutputStream(it).use { out ->
            if (out != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                Toast.makeText(context, "تم حفظ الصورة بنجاح", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
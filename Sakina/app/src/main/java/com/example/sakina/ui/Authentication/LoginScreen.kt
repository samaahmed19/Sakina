package com.example.sakina.ui.authentication

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.sakina.R
import kotlin.random.Random

private val BackgroundDark = Color(0xFF1A1B2E)
private val BackgroundPurple = Color(0xFF16213E)
private val BackgroundDeep = Color(0xFF0F0F23)
private val LightPurple = Color(0xFFB8A4D4)
private val LightPurpleMuted = Color(0xFF9B8FCC)
private val GoldenCrescent = Color(0xFFF5D742)
private val LocationTeal = Color(0xFF5DD9C1)
private val LocationTealLight = Color(0xFF7FE8D6)
private val GlassCardColor = Color.White.copy(alpha = 0.08f)
private val GlassCardGradient = Brush.verticalGradient(
    listOf(
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.06f),
        Color.White.copy(alpha = 0.12f)
    )
)
private val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFA78BFA),
        Color(0xFF818CF8),
        Color(0xFF60A5FA)
    )
)
private val InputFieldBg = Color.White.copy(alpha = 0.1f)
private val TextMuted = Color.White.copy(alpha = 0.6f)
private val TextSubtle = Color.White.copy(alpha = 0.5f)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.fetchLocation()
    }

    fun onSetLocationClick() {
        if (hasLocationPermission) {
            viewModel.fetchLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BackgroundDark,
                        BackgroundPurple,
                        BackgroundDeep
                    )
                )
            )
    ) {
        // ⭐ النجوم
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(150) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f + Random.nextFloat() * 0.5f),
                    radius = 1f + Random.nextFloat() * 2f,
                    center = Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                )
            }
        }

        // البطاقة الزجاجية
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(GlassCardGradient)
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // أيقونة التطبيق
                SakinaLogoIcon()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ميصال",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightPurple
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "رحلتك الروحانية تبدأ هنا",
                    fontSize = 14.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(28.dp))

                GlassTextField(
                    hint = "الاسم",
                    value = state.name,
                    onChange = viewModel::onNameChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // صندوق معلومات الموقع
                LocationInfoBox()

                Spacer(modifier = Modifier.height(16.dp))

                // زر تحديد الموقع
                SetLocationButton(
                    onClick = { onSetLocationClick() },
                    isLoading = state.isLoadingLocation,
                    locationText = state.location
                )

                Spacer(modifier = Modifier.height(24.dp))

                GlowButton(
                    text = "ابدأ رحلتك الروحانية",
                    loading = state.isLoading,
                    onClick = viewModel::onStartClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "نحفظ خصوصيتك ولا نشارك بياناتك مع أي طرف ثالث",
                    fontSize = 12.sp,
                    color = TextSubtle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SakinaLogoIcon() {
    Image(
        painter = painterResource(id = R.drawable.splash),
        contentDescription = "سَكِينَة",
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(50)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun GlassTextField(
    hint: String,
    value: String,
    onChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onChange,
        placeholder = {
            Text(hint, color = TextMuted)
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputFieldBg,
            unfocusedContainerColor = InputFieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun LocationInfoBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InputFieldBg)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LocationTeal,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "الموقع مطلوب",
                    color = LocationTealLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "نستخدم موقعك لتحديد مواقيت الصلاة والأذكار المرتبطة بالمكان بدقة",
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SetLocationButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    locationText: String = ""
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InputFieldBg)
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = LocationTeal,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = LocationTeal,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (locationText.isNotBlank()) "تم تحديد الموقع ✓" else "تحديد الموقع",
                color = if (locationText.isNotBlank()) LocationTealLight else TextMuted,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun GlowButton(
    text: String,
    loading: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0xFF818CF8),
                spotColor = Color(0xFF60A5FA)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(brush = ButtonGradient)
            .clickable(enabled = !loading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* Preview */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    val fakeState = remember {
        object {
            val name = "محمد"
            val isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundDark, BackgroundPurple, BackgroundDeep)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(100) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = 2f,
                    center = Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(GlassCardGradient)
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SakinaLogoIcon()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "سَكَن",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightPurple
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "رحلتك الروحانية تبدأ هنا",
                    fontSize = 14.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(28.dp))
                GlassTextField(
                    hint = "الاسم",
                    value = fakeState.name,
                    onChange = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                LocationInfoBox()
                Spacer(modifier = Modifier.height(16.dp))
                SetLocationButton(onClick = {}, isLoading = false, locationText = "")
                Spacer(modifier = Modifier.height(24.dp))
                GlowButton(
                    text = "ابدأ رحلتك الروحانية",
                    loading = fakeState.isLoading,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "نحفظ خصوصيتك ولا نشارك بياناتك مع أي طرف ثالث",
                    fontSize = 12.sp,
                    color = TextSubtle,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

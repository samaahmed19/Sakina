package com.example.sakina.ui.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val locations = listOf("القاهرة", "الإسكندرية")
    val languages = listOf("العربية", "English")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            //Profile
            ProfileSection(state.name, state.email) {}
            Spacer(Modifier.height(8.dp))
            Divider()

            //Location
            LocationSection(state.location) { showLocationDialog = true }
            Spacer(Modifier.height(8.dp))
            Divider()

            //Notifications
            NotificationsSection(
                prayer = state.prayerNotifications,
                azkar = state.azkarNotifications,
                onPrayerChange = viewModel::updatePrayerNotifications,
                onAzkarChange = viewModel::updateAzkarNotifications
            )
            Spacer(Modifier.height(8.dp))
            Divider()

            //Appearance
            AppearanceSection(state.darkMode, viewModel::updateDarkMode)
            Spacer(Modifier.height(8.dp))
            Divider()

            //Language
            LanguageSection(state.language) { showLanguageDialog = true }

            //Location Dialog
            if (showLocationDialog) {
                AlertDialog(
                    onDismissRequest = { showLocationDialog = false },
                    title = { Text("اختر موقعك") },
                    text = {
                        Column {
                            locations.forEach { city ->
                                Text(
                                    text = city,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateLocation(city)
                                            showLocationDialog = false
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {}
                )
            }

            //Language Dialog
            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text("اختر اللغة") },
                    text = {
                        Column {
                            languages.forEach { lang ->
                                Text(
                                    text = lang,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.updateLanguage(lang)
                                            showLanguageDialog = false
                                        }
                                        .padding(12.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {}
                )
            }
        }
    }
}

//Sections
@Composable
fun ProfileSection(name: String, email: String, onEdit: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = email, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "تعديل البيانات",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onEdit() }
            )
        }
    }
}

@Composable
fun LocationSection(location: String, onChange: () -> Unit) =
    SettingsItem("الموقع", location, onChange)

@Composable
fun NotificationsSection(
    prayer: Boolean,
    azkar: Boolean,
    onPrayerChange: (Boolean) -> Unit,
    onAzkarChange: (Boolean) -> Unit
) {
    SettingsSwitchItem("تنبيهات الصلاة", prayer, onPrayerChange)
    SettingsSwitchItem("تنبيهات الأذكار", azkar, onAzkarChange)
}

@Composable
fun AppearanceSection(darkMode: Boolean, onThemeChange: (Boolean) -> Unit) =
    SettingsSwitchItem("الوضع الليلي", darkMode, onThemeChange)

@Composable
fun LanguageSection(currentLang: String, onChange: () -> Unit) =
    SettingsItem("اللغة", currentLang, onChange)

//Reusable
@Composable
fun SettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (pressed) 0.3f else 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                pressed = true
                onClick()
            }
            .alpha(alpha)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun SettingsSwitchItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val animatedChecked by animateFloatAsState(if (checked) 1f else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

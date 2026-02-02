package com.example.sakina.ui.settings

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showEditNameDialog by remember { mutableStateOf(false) }
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

            // Profile
            ProfileSection(
                name = state.name,
                email = state.email
            ) {
                showEditNameDialog = true
            }

            Spacer(Modifier.height(8.dp))
            Divider()

            // Location
            LocationSection(state.location) {
                showLocationDialog = true
            }

            Spacer(Modifier.height(8.dp))
            Divider()

            // Notifications
            NotificationsSection(
                prayer = state.prayerNotifications,
                azkar = state.azkarNotifications,
                onPrayerChange = viewModel::updatePrayerNotifications,
                onAzkarChange = viewModel::updateAzkarNotifications
            )

            Spacer(Modifier.height(8.dp))
            Divider()

            // Appearance
            AppearanceSection(
                darkMode = state.darkMode,
                onThemeChange = viewModel::updateDarkMode
            )

            Spacer(Modifier.height(8.dp))
            Divider()

            // Language
            LanguageSection(state.language) {
                showLanguageDialog = true
            }
        }

        // Dialogs

        // Edit Name Dialog
        if (showEditNameDialog) {
            var newName by remember { mutableStateOf(state.name) }

            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                title = { Text("تعديل الاسم") },
                text = {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("الاسم") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newName.isNotBlank()) {
                                viewModel.updateName(newName)
                                showEditNameDialog = false
                            }
                        }
                    ) {
                        Text("حفظ")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("إلغاء")
                    }
                }
            )
        }

        // Location Dialog
        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = { Text("اختر موقعك") },
                text = {
                    Column {
                        locations.forEach { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateLocation(city)
                                        showLocationDialog = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = city == state.location,
                                    onClick = null
                                )
                                Text(city)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        // Language Dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("اختر اللغة") },
                text = {
                    Column {
                        languages.forEach { lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.updateLanguage(lang)
                                        showLanguageDialog = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = lang == state.language,
                                    onClick = null
                                )
                                Text(lang)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }
    }
}

//Sections

@Composable
fun ProfileSection(
    name: String,
    email: String,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp)
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
fun AppearanceSection(
    darkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) =
    SettingsSwitchItem("الوضع الليلي", darkMode, onThemeChange)

@Composable
fun LanguageSection(
    currentLang: String,
    onChange: () -> Unit
) =
    SettingsItem("اللغة", currentLang, onChange)

// Reusable

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(title, fontSize = 16.sp)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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

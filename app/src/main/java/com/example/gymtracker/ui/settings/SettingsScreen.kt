package com.example.gymtracker.ui.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtracker.ui.viewmodel.SettingsViewModel
import com.example.gymtracker.util.DataExporter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState(initial = false)
    val exportInProgress by viewModel.exportInProgress.collectAsState(initial = false)
    
    var showExportDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Настройки",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Темная тема
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DarkMode,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(
                        text = "Темная тема",
                        fontSize = 16.sp
                    )
                }
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { viewModel.setDarkTheme(it) }
                )
            }
        }

        // Уведомления
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = { showNotificationDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Напоминания о тренировках",
                    fontSize = 16.sp
                )
            }
        }

        // Экспорт данных
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = { showExportDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FileDownload,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Экспорт данных",
                    fontSize = 16.sp
                )
            }
        }

        // Резервное копирование
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = { viewModel.backupToGoogleDrive(context) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Резервное копирование (Google Drive)",
                    fontSize = 16.sp
                )
            }
        }

        if (exportInProgress) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }

    if (showExportDialog) {
        ExportDataDialog(
            onDismiss = { showExportDialog = false },
            onExportJson = {
                scope.launch {
                    viewModel.exportData(context, "json")
                }
                showExportDialog = false
            },
            onExportCsv = {
                scope.launch {
                    viewModel.exportData(context, "csv")
                }
                showExportDialog = false
            }
        )
    }

    if (showNotificationDialog) {
        NotificationSettingsDialog(
            onDismiss = { showNotificationDialog = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun ExportDataDialog(
    onDismiss: () -> Unit,
    onExportJson: () -> Unit,
    onExportCsv: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Экспорт данных") },
        text = {
            Column {
                Text("Выберите формат экспорта:")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onExportJson,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Экспорт в JSON")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onExportCsv,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Экспорт в CSV")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun NotificationSettingsDialog(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel
) {
    var time by remember { mutableStateOf("18:00") }
    var enabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Настройка напоминаний") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Включить напоминания")
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Время напоминания (ЧЧ:ММ)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (enabled) {
                        viewModel.setNotificationTime(time)
                    } else {
                        viewModel.disableNotifications()
                    }
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

package com.yunqi.app.feature.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R

@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    onPregnancyProfileClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var notificationsAllowed by remember { mutableStateOf(context.canPostNotifications()) }
    var pendingNotificationRequest by remember { mutableStateOf<NotificationPermissionRequest?>(null) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationsAllowed = granted
        when (pendingNotificationRequest) {
            NotificationPermissionRequest.Appointment -> viewModel.setAppointmentRemindersEnabled(granted)
            NotificationPermissionRequest.Daily -> viewModel.setDailyReminderEnabled(granted, uiState.dailyReminderTime)
            null -> Unit
        }
        pendingNotificationRequest = null
    }
    DisposableEffect(context, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                notificationsAllowed = context.canPostNotifications()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(notificationsAllowed, uiState.appointmentRemindersEnabled) {
        if (!notificationsAllowed && uiState.appointmentRemindersEnabled) {
            viewModel.setAppointmentRemindersEnabled(false)
        }
    }
    LaunchedEffect(notificationsAllowed, uiState.dailyReminderEnabled, uiState.dailyReminderTime) {
        if (!notificationsAllowed && uiState.dailyReminderEnabled) {
            viewModel.setDailyReminderEnabled(false, uiState.dailyReminderTime)
        }
    }

    SettingsScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        notificationsAllowed = notificationsAllowed,
        onPregnancyProfileClick = onPregnancyProfileClick,
        onReminderEnabledChange = { enabled ->
            if (!enabled) {
                viewModel.setAppointmentRemindersEnabled(false)
            } else if (context.mustRequestNotificationPermission() && !notificationsAllowed) {
                pendingNotificationRequest = NotificationPermissionRequest.Appointment
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.setAppointmentRemindersEnabled(true)
            }
        },
        onDailyReminderEnabledChange = { enabled ->
            if (!enabled) {
                viewModel.setDailyReminderEnabled(false, uiState.dailyReminderTime)
            } else if (context.mustRequestNotificationPermission() && !notificationsAllowed) {
                pendingNotificationRequest = NotificationPermissionRequest.Daily
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.setDailyReminderEnabled(true, uiState.dailyReminderTime)
            }
        },
        onDailyReminderTimeSelected = viewModel::setDailyReminderTime,
        onClearAllLocalData = viewModel::clearAllLocalData,
    )
}

private enum class NotificationPermissionRequest {
    Appointment,
    Daily,
}

@Composable
private fun SettingsScreen(
    contentPadding: PaddingValues,
    uiState: SettingsUiState,
    notificationsAllowed: Boolean,
    onPregnancyProfileClick: () -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onDailyReminderEnabledChange: (Boolean) -> Unit,
    onDailyReminderTimeSelected: (String) -> Unit,
    onClearAllLocalData: () -> Unit,
) {
    val remindersChecked = uiState.appointmentRemindersEnabled && notificationsAllowed
    val dailyReminderChecked = uiState.dailyReminderEnabled && notificationsAllowed
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.settings_clear_data_confirm_title)) },
            text = { Text(stringResource(R.string.settings_clear_data_confirm_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirm = false
                        onClearAllLocalData()
                    },
                ) {
                    Text(stringResource(R.string.settings_clear_data_confirm_action))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.calendar_delete_cancel_action))
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.settings_pregnancy_profile),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(stringResource(R.string.settings_pregnancy_profile_body))
        Button(
            onClick = onPregnancyProfileClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_edit_pregnancy_profile))
        }
        HorizontalDivider()
        Text(
            text = stringResource(R.string.settings_reminders),
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_appointment_reminders),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.settings_appointment_reminders_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (uiState.appointmentRemindersEnabled && !notificationsAllowed) {
                    Text(
                        text = stringResource(R.string.settings_notifications_permission_missing),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Switch(
                checked = remindersChecked,
                onCheckedChange = onReminderEnabledChange,
            )
        }
        DailyReminderSetting(
            enabled = dailyReminderChecked,
            selectedTime = uiState.dailyReminderTime,
            onEnabledChange = onDailyReminderEnabledChange,
            onTimeSelected = onDailyReminderTimeSelected,
        )
        HorizontalDivider()
        Text(
            text = stringResource(R.string.settings_privacy),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.settings_local_data_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = { showClearConfirm = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_clear_local_data))
        }
        Text(
            text = stringResource(R.string.medical_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DailyReminderSetting(
    enabled: Boolean,
    selectedTime: String,
    onEnabledChange: (Boolean) -> Unit,
    onTimeSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_daily_reminders),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.settings_daily_reminders_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange,
            )
        }
        if (enabled) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                dailyReminderTimeOptions().forEach { option ->
                    FilterChip(
                        selected = selectedTime == option.time,
                        onClick = { onTimeSelected(option.time) },
                        label = { Text(stringResource(option.labelResId)) },
                    )
                }
            }
        }
    }
}

private data class DailyReminderTimeOption(
    val time: String,
    val labelResId: Int,
)

private fun dailyReminderTimeOptions(): List<DailyReminderTimeOption> = listOf(
    DailyReminderTimeOption("09:00", R.string.settings_daily_reminder_morning),
    DailyReminderTimeOption("13:00", R.string.settings_daily_reminder_noon),
    DailyReminderTimeOption("20:00", R.string.settings_daily_reminder_evening),
)

private fun Context.mustRequestNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

private fun Context.canPostNotifications(): Boolean {
    if (!mustRequestNotificationPermission()) return true
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
}

package com.yunqi.app.feature.settings

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings as AndroidSettings
import android.widget.Toast
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import com.yunqi.app.data.local.DailyReminderPreference
import com.yunqi.app.domain.reminder.DailyReminderType
import com.yunqi.app.notification.canPostYunqiNotifications
import com.yunqi.app.notification.hasYunqiNotificationRuntimePermission
import kotlinx.coroutines.launch

@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    onPregnancyProfileClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var notificationsAllowed by remember { mutableStateOf(context.canPostNotifications()) }
    var runtimeNotificationPermissionGranted by remember {
        mutableStateOf(context.hasNotificationRuntimePermission())
    }
    var pendingNotificationRequest by remember { mutableStateOf<NotificationPermissionRequest?>(null) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        val runtimePermissionGranted = context.hasNotificationRuntimePermission()
        val canPostNotifications = context.canPostNotifications()
        runtimeNotificationPermissionGranted = runtimePermissionGranted
        notificationsAllowed = canPostNotifications
        when (pendingNotificationRequest) {
            NotificationPermissionRequest.Appointment -> {
                viewModel.setAppointmentRemindersEnabled(granted && canPostNotifications)
            }

            is NotificationPermissionRequest.Daily -> {
                val request = pendingNotificationRequest as NotificationPermissionRequest.Daily
                viewModel.setDailyReminderEnabled(
                    type = request.type,
                    enabled = granted && canPostNotifications,
                    time = request.time,
                    customMessage = request.customMessage,
                )
            }

            null -> Unit
        }
        pendingNotificationRequest = null
    }
    DisposableEffect(context, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                runtimeNotificationPermissionGranted = context.hasNotificationRuntimePermission()
                notificationsAllowed = context.canPostNotifications()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(notificationsAllowed, uiState.dailyReminders) {
        when (notificationAvailabilityAction(notificationsAllowed)) {
            NotificationAvailabilityAction.PreserveReminderPreferences -> Unit
            NotificationAvailabilityAction.SyncScheduledReminderWork -> {
                viewModel.syncDailyReminders(uiState.dailyReminders)
            }
        }
    }
    LaunchedEffect(
        notificationsAllowed,
        uiState.appointmentRemindersEnabled,
        uiState.appointmentReminderLeadMinutes,
    ) {
        when (appointmentReminderAvailabilityAction(notificationsAllowed, uiState.appointmentRemindersEnabled)) {
            AppointmentReminderAvailabilityAction.PreserveAppointmentReminderPreference -> Unit
            AppointmentReminderAvailabilityAction.SyncScheduledAppointmentWork -> {
                viewModel.syncAppointmentReminders(uiState.appointmentReminderLeadMinutes)
            }
        }
    }

    SettingsScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        notificationsAllowed = notificationsAllowed,
        runtimeNotificationPermissionGranted = runtimeNotificationPermissionGranted,
        onPregnancyProfileClick = onPregnancyProfileClick,
        onReminderEnabledChange = { enabled ->
            if (!enabled) {
                viewModel.setAppointmentRemindersEnabled(false)
            } else if (context.shouldRequestNotificationRuntimePermission()) {
                pendingNotificationRequest = NotificationPermissionRequest.Appointment
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (!notificationsAllowed) {
                context.openAppNotificationSettings()
            } else {
                viewModel.setAppointmentRemindersEnabled(true)
            }
        },
        onDailyReminderEnabledChange = { reminder, enabled ->
            if (!enabled) {
                viewModel.setDailyReminderEnabled(reminder.type, false, reminder.time)
            } else if (context.shouldRequestNotificationRuntimePermission()) {
                pendingNotificationRequest = NotificationPermissionRequest.Daily(
                    type = reminder.type,
                    time = reminder.time,
                    customMessage = reminder.customMessage,
                )
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (!notificationsAllowed) {
                context.openAppNotificationSettings()
            } else {
                viewModel.setDailyReminderEnabled(
                    type = reminder.type,
                    enabled = true,
                    time = reminder.time,
                    customMessage = reminder.customMessage,
                )
            }
        },
        onDailyReminderTimeSelected = viewModel::setDailyReminderTime,
        onDailyReminderCustomMessageChange = viewModel::setDailyReminderCustomMessage,
        onAppointmentReminderLeadSelected = viewModel::setAppointmentReminderLeadMinutes,
        onOpenNotificationSettings = {
            context.openAppNotificationSettings()
        },
        onExportRecords = {
            coroutineScope.launch {
                val uri = viewModel.exportCalendarRecordsCsv()
                context.shareCsvExport(uri)
            }
        },
        onClearPregnancyProfile = viewModel::clearPregnancyProfileAndReminders,
        onClearAllLocalData = viewModel::clearAllLocalData,
    )
}

private sealed interface NotificationPermissionRequest {
    data object Appointment : NotificationPermissionRequest

    data class Daily(
        val type: DailyReminderType,
        val time: String,
        val customMessage: String,
    ) : NotificationPermissionRequest
}

internal enum class NotificationAvailabilityAction {
    PreserveReminderPreferences,
    SyncScheduledReminderWork,
}

internal fun notificationAvailabilityAction(notificationsAllowed: Boolean): NotificationAvailabilityAction =
    if (notificationsAllowed) {
        NotificationAvailabilityAction.SyncScheduledReminderWork
    } else {
        NotificationAvailabilityAction.PreserveReminderPreferences
    }

internal enum class AppointmentReminderAvailabilityAction {
    PreserveAppointmentReminderPreference,
    SyncScheduledAppointmentWork,
}

internal fun appointmentReminderAvailabilityAction(
    notificationsAllowed: Boolean,
    appointmentRemindersEnabled: Boolean,
): AppointmentReminderAvailabilityAction =
    if (notificationsAllowed && appointmentRemindersEnabled) {
        AppointmentReminderAvailabilityAction.SyncScheduledAppointmentWork
    } else {
        AppointmentReminderAvailabilityAction.PreserveAppointmentReminderPreference
    }

@Composable
private fun SettingsScreen(
    contentPadding: PaddingValues,
    uiState: SettingsUiState,
    notificationsAllowed: Boolean,
    runtimeNotificationPermissionGranted: Boolean,
    onPregnancyProfileClick: () -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onDailyReminderEnabledChange: (DailyReminderPreference, Boolean) -> Unit,
    onDailyReminderTimeSelected: (DailyReminderType, String, String) -> Unit,
    onDailyReminderCustomMessageChange: (DailyReminderType, String, Boolean, String) -> Unit,
    onAppointmentReminderLeadSelected: (Long) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onExportRecords: () -> Unit,
    onClearPregnancyProfile: () -> Unit,
    onClearAllLocalData: () -> Unit,
) {
    val remindersChecked = uiState.appointmentRemindersEnabled && notificationsAllowed
    var showProfileClearConfirm by remember { mutableStateOf(false) }
    var showClearConfirm by remember { mutableStateOf(false) }

    if (showProfileClearConfirm) {
        AlertDialog(
            onDismissRequest = { showProfileClearConfirm = false },
            title = { Text(stringResource(R.string.settings_clear_profile_confirm_title)) },
            text = { Text(stringResource(R.string.settings_clear_profile_confirm_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showProfileClearConfirm = false
                        onClearPregnancyProfile()
                    },
                ) {
                    Text(stringResource(R.string.settings_clear_profile_confirm_action))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showProfileClearConfirm = false }) {
                    Text(stringResource(R.string.calendar_delete_cancel_action))
                }
            },
        )
    }

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
        OutlinedButton(
            onClick = { showProfileClearConfirm = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_clear_pregnancy_profile))
        }
        HorizontalDivider()
        Text(
            text = stringResource(R.string.settings_reminders),
            style = MaterialTheme.typography.titleMedium,
        )
        if (!notificationsAllowed) {
            Text(
                text = stringResource(
                    if (runtimeNotificationPermissionGranted) {
                        R.string.settings_notifications_switch_guidance
                    } else {
                        R.string.settings_notifications_permission_guidance
                    },
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
            OutlinedButton(
                onClick = onOpenNotificationSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.settings_open_notification_settings))
            }
        }
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
        AppointmentReminderLeadSettings(
            selectedLeadMinutes = uiState.appointmentReminderLeadMinutes,
            onLeadSelected = onAppointmentReminderLeadSelected,
        )
        DailyReminderSettings(
            reminders = uiState.dailyReminders,
            notificationsAllowed = notificationsAllowed,
            onEnabledChange = onDailyReminderEnabledChange,
            onTimeSelected = onDailyReminderTimeSelected,
            onCustomMessageChange = onDailyReminderCustomMessageChange,
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
        Button(
            onClick = onExportRecords,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_export_records_csv))
        }
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
        Text(
            text = stringResource(R.string.emergency_attention_notice),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun AppointmentReminderLeadSettings(
    selectedLeadMinutes: Long,
    onLeadSelected: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.settings_appointment_reminder_lead_time),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.settings_appointment_reminder_lead_time_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            appointmentReminderLeadOptions().forEach { option ->
                FilterChip(
                    selected = selectedLeadMinutes == option.minutes,
                    onClick = { onLeadSelected(option.minutes) },
                    label = { Text(stringResource(option.labelResId)) },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DailyReminderSettings(
    reminders: List<DailyReminderPreference>,
    notificationsAllowed: Boolean,
    onEnabledChange: (DailyReminderPreference, Boolean) -> Unit,
    onTimeSelected: (DailyReminderType, String, String) -> Unit,
    onCustomMessageChange: (DailyReminderType, String, Boolean, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.settings_daily_reminders),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = stringResource(R.string.settings_daily_reminders_body),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        reminders.forEach { reminder ->
            val checked = reminder.enabled && notificationsAllowed
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = stringResource(reminder.type.titleResId()),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(reminder.type.bodyResId()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (reminder.enabled && !notificationsAllowed) {
                            Text(
                                text = stringResource(R.string.settings_notifications_permission_missing),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    Switch(
                        checked = checked,
                        onCheckedChange = { enabled -> onEnabledChange(reminder, enabled) },
                    )
                }
                if (reminder.type == DailyReminderType.Custom) {
                    OutlinedTextField(
                        value = reminder.customMessage,
                        onValueChange = { message ->
                            onCustomMessageChange(reminder.type, message, reminder.enabled, reminder.time)
                        },
                        label = { Text(stringResource(R.string.settings_daily_reminder_custom_message_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                }
                if (checked) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        dailyReminderTimeOptions().forEach { option ->
                            FilterChip(
                                selected = reminder.time == option.time,
                                onClick = { onTimeSelected(reminder.type, option.time, reminder.customMessage) },
                                label = { Text(stringResource(option.labelResId)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class DailyReminderTimeOption(
    val time: String,
    val labelResId: Int,
)

private data class AppointmentReminderLeadOption(
    val minutes: Long,
    val labelResId: Int,
)

private fun appointmentReminderLeadOptions(): List<AppointmentReminderLeadOption> = listOf(
    AppointmentReminderLeadOption(60L, R.string.settings_appointment_reminder_lead_1_hour),
    AppointmentReminderLeadOption(6L * 60L, R.string.settings_appointment_reminder_lead_6_hours),
    AppointmentReminderLeadOption(24L * 60L, R.string.settings_appointment_reminder_lead_1_day),
)

private fun dailyReminderTimeOptions(): List<DailyReminderTimeOption> = listOf(
    DailyReminderTimeOption("09:00", R.string.settings_daily_reminder_morning),
    DailyReminderTimeOption("13:00", R.string.settings_daily_reminder_noon),
    DailyReminderTimeOption("17:00", R.string.settings_daily_reminder_afternoon),
    DailyReminderTimeOption("20:00", R.string.settings_daily_reminder_evening),
)

private fun DailyReminderType.titleResId(): Int = when (this) {
    DailyReminderType.Weight -> R.string.settings_daily_reminder_weight
    DailyReminderType.FetalMovement -> R.string.settings_daily_reminder_fetal_movement
    DailyReminderType.Vitamin -> R.string.settings_daily_reminder_vitamin
    DailyReminderType.Water -> R.string.settings_daily_reminder_water
    DailyReminderType.Exercise -> R.string.settings_daily_reminder_exercise
    DailyReminderType.Custom -> R.string.settings_daily_reminder_custom
}

private fun DailyReminderType.bodyResId(): Int = when (this) {
    DailyReminderType.Weight -> R.string.settings_daily_reminder_weight_body
    DailyReminderType.FetalMovement -> R.string.settings_daily_reminder_fetal_movement_body
    DailyReminderType.Vitamin -> R.string.settings_daily_reminder_vitamin_body
    DailyReminderType.Water -> R.string.settings_daily_reminder_water_body
    DailyReminderType.Exercise -> R.string.settings_daily_reminder_exercise_body
    DailyReminderType.Custom -> R.string.settings_daily_reminder_custom_body
}

private fun Context.mustRequestNotificationPermission(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

private fun Context.shouldRequestNotificationRuntimePermission(): Boolean =
    mustRequestNotificationPermission() && !hasYunqiNotificationRuntimePermission(this)

private fun Context.hasNotificationRuntimePermission(): Boolean =
    hasYunqiNotificationRuntimePermission(this)

private fun Context.canPostNotifications(): Boolean {
    return canPostYunqiNotifications(this)
}

private fun Context.openAppNotificationSettings() {
    val notificationSettingsIntent = Intent(AndroidSettings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(AndroidSettings.EXTRA_APP_PACKAGE, packageName)
    val fallbackIntent = Intent(AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(android.net.Uri.parse("package:$packageName"))

    try {
        startActivity(notificationSettingsIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(fallbackIntent)
    }
}

private fun Context.shareCsvExport(uri: android.net.Uri) {
    val sendIntent = Intent(Intent.ACTION_SEND)
        .setType("text/csv")
        .putExtra(Intent.EXTRA_STREAM, uri)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        .apply {
            clipData = ClipData.newUri(contentResolver, getString(R.string.settings_export_records_csv), uri)
        }
    try {
        startActivity(
            Intent.createChooser(
                sendIntent,
                getString(R.string.settings_export_records_csv),
            ),
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, R.string.settings_export_records_unavailable, Toast.LENGTH_SHORT).show()
    }
}

package com.yunqi.app.feature.calendar

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import kotlinx.coroutines.launch

@Composable
fun CalendarRoute(
    contentPadding: PaddingValues,
    viewModel: CalendarViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onSelectDate = viewModel::selectDate,
        onSaveRecord = viewModel::save,
        onDeleteRecord = viewModel::delete,
    )
}

@Composable
private fun CalendarScreen(
    contentPadding: PaddingValues,
    uiState: CalendarUiState,
    onSelectDate: (String) -> CalendarRecordActionResult,
    onSaveRecord: suspend (CalendarRecordInput) -> CalendarRecordActionResult,
    onDeleteRecord: suspend (String) -> Unit,
) {
    var selectedDateText by remember(uiState.selectedDate) { mutableStateOf(uiState.selectedDate.toString()) }
    var recordType by remember { mutableStateOf(CalendarRecordType.Appointment) }
    var note by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var fetalMovementCount by remember { mutableStateOf("") }
    var appointmentTime by remember { mutableStateOf("") }
    var appointmentLocation by remember { mutableStateOf("") }
    var errorMessageResId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        DateSelector(
            selectedDateText = selectedDateText,
            onSelectedDateTextChange = { selectedDateText = it },
            onSelectDate = {
                errorMessageResId = onSelectDate(selectedDateText).toErrorMessageResId()
            },
        )

        RecordForm(
            recordType = recordType,
            onRecordTypeChange = {
                recordType = it
                errorMessageResId = null
            },
            note = note,
            onNoteChange = { note = it },
            weightKg = weightKg,
            onWeightKgChange = { weightKg = it },
            fetalMovementCount = fetalMovementCount,
            onFetalMovementCountChange = { fetalMovementCount = it },
            appointmentTime = appointmentTime,
            onAppointmentTimeChange = { appointmentTime = it },
            appointmentLocation = appointmentLocation,
            onAppointmentLocationChange = { appointmentLocation = it },
            onSave = {
                coroutineScope.launch {
                    val result = onSaveRecord(
                        CalendarRecordInput(
                            date = selectedDateText,
                            type = recordType,
                            note = note,
                            weightKg = weightKg,
                            fetalMovementCount = fetalMovementCount,
                            appointmentTime = appointmentTime,
                            appointmentLocation = appointmentLocation,
                        ),
                    )
                    errorMessageResId = result.toErrorMessageResId()
                    if (result == CalendarRecordActionResult.Success) {
                        note = ""
                        weightKg = ""
                        fetalMovementCount = ""
                        appointmentTime = ""
                        appointmentLocation = ""
                    }
                }
            },
        )

        errorMessageResId?.let { messageResId ->
            Text(
                text = stringResource(messageResId),
                color = MaterialTheme.colorScheme.error,
            )
        }

        RecordList(
            records = uiState.records,
            onDeleteRecord = onDeleteRecord,
        )
    }
}

@Composable
private fun DateSelector(
    selectedDateText: String,
    onSelectedDateTextChange: (String) -> Unit,
    onSelectDate: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = selectedDateText,
                onValueChange = onSelectedDateTextChange,
                label = { Text(stringResource(R.string.calendar_selected_date)) },
                placeholder = { Text(stringResource(R.string.calendar_selected_date_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSelectDate,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.calendar_view_date))
            }
        }
    }
}

@Composable
private fun RecordForm(
    recordType: CalendarRecordType,
    onRecordTypeChange: (CalendarRecordType) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    weightKg: String,
    onWeightKgChange: (String) -> Unit,
    fetalMovementCount: String,
    onFetalMovementCountChange: (String) -> Unit,
    appointmentTime: String,
    onAppointmentTimeChange: (String) -> Unit,
    appointmentLocation: String,
    onAppointmentLocationChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.calendar_add_record),
                style = MaterialTheme.typography.titleMedium,
            )
            RecordTypeSelector(
                recordType = recordType,
                onRecordTypeChange = onRecordTypeChange,
            )

            when (recordType) {
                CalendarRecordType.Appointment -> {
                    OutlinedTextField(
                        value = appointmentTime,
                        onValueChange = onAppointmentTimeChange,
                        label = { Text(stringResource(R.string.calendar_appointment_time_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = appointmentLocation,
                        onValueChange = onAppointmentLocationChange,
                        label = { Text(stringResource(R.string.calendar_appointment_location_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                CalendarRecordType.Weight -> OutlinedTextField(
                    value = weightKg,
                    onValueChange = onWeightKgChange,
                    label = { Text(stringResource(R.string.calendar_weight_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                CalendarRecordType.FetalMovement -> OutlinedTextField(
                    value = fetalMovementCount,
                    onValueChange = onFetalMovementCountChange,
                    label = { Text(stringResource(R.string.calendar_fetal_movement_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                CalendarRecordType.Note -> Unit
            }

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text(stringResource(R.string.calendar_note_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
            )
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.calendar_save_record))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RecordTypeSelector(
    recordType: CalendarRecordType,
    onRecordTypeChange: (CalendarRecordType) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CalendarRecordType.entries.forEach { type ->
            FilterChip(
                selected = recordType == type,
                onClick = { onRecordTypeChange(type) },
                label = { Text(type.toDisplayText()) },
            )
        }
    }
}

@Composable
private fun RecordList(
    records: List<CalendarRecord>,
    onDeleteRecord: suspend (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = stringResource(R.string.calendar_selected_date_records),
        style = MaterialTheme.typography.titleMedium,
    )
    if (records.isEmpty()) {
        Text(
            text = stringResource(R.string.calendar_empty_records),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        records.forEach { record ->
            RecordCard(
                record = record,
                onDelete = {
                    coroutineScope.launch { onDeleteRecord(record.id) }
                },
            )
        }
    }
}

@Composable
private fun RecordCard(
    record: CalendarRecord,
    onDelete: () -> Unit,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.calendar_delete_confirm_title)) },
            text = { Text(stringResource(R.string.calendar_delete_confirm_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                ) {
                    Text(stringResource(R.string.calendar_delete_confirm_action))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.calendar_delete_cancel_action))
                }
            },
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = record.type.toDisplayText(),
                    style = MaterialTheme.typography.titleMedium,
                )
                Button(onClick = { showDeleteConfirm = true }) {
                    Text(stringResource(R.string.calendar_delete_record))
                }
            }
            record.weightKg?.let {
                Text(stringResource(R.string.calendar_record_weight_value, it))
            }
            record.fetalMovementCount?.let {
                Text(stringResource(R.string.calendar_record_fetal_movement_value, it))
            }
            record.appointmentTime?.let {
                Text(stringResource(R.string.calendar_record_appointment_time, it))
            }
            record.appointmentLocation?.let {
                Text(stringResource(R.string.calendar_record_appointment_location, it))
            }
            if (record.note.isNotBlank()) {
                Text(record.note)
            }
        }
    }
}

@Composable
private fun CalendarRecordType.toDisplayText(): String = when (this) {
    CalendarRecordType.Appointment -> stringResource(R.string.calendar_record_type_appointment)
    CalendarRecordType.Weight -> stringResource(R.string.calendar_record_type_weight)
    CalendarRecordType.FetalMovement -> stringResource(R.string.calendar_record_type_fetal_movement)
    CalendarRecordType.Note -> stringResource(R.string.calendar_record_type_note)
}

private fun CalendarRecordActionResult.toErrorMessageResId(): Int? = when (this) {
    CalendarRecordActionResult.Success -> null
    CalendarRecordActionResult.InvalidDate -> R.string.calendar_error_invalid_date
    CalendarRecordActionResult.InvalidWeight -> R.string.calendar_error_invalid_weight
    CalendarRecordActionResult.InvalidFetalMovement -> R.string.calendar_error_invalid_fetal_movement
}

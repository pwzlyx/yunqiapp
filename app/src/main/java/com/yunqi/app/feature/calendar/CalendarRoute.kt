package com.yunqi.app.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import com.yunqi.app.domain.calendar.CalendarRecord
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.feature.common.isoDateKeyboardOptions
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CalendarRoute(
    contentPadding: PaddingValues,
    initialRecordType: CalendarRecordType? = null,
    viewModel: CalendarViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onSelectDate = viewModel::selectDate,
        onSelectCalendarDate = viewModel::selectDate,
        initialRecordType = initialRecordType,
        onSaveRecord = viewModel::save,
        onDeleteRecord = viewModel::delete,
    )
}

@Composable
private fun CalendarScreen(
    contentPadding: PaddingValues,
    uiState: CalendarUiState,
    onSelectDate: (String) -> CalendarRecordActionResult,
    onSelectCalendarDate: (LocalDate) -> Unit,
    initialRecordType: CalendarRecordType?,
    onSaveRecord: suspend (CalendarRecordInput) -> CalendarRecordActionResult,
    onDeleteRecord: suspend (String) -> Unit,
) {
    var selectedDateText by remember(uiState.selectedDate) { mutableStateOf(uiState.selectedDate.toString()) }
    var recordType by remember(initialRecordType) {
        mutableStateOf(initialRecordType ?: CalendarRecordType.Appointment)
    }
    var note by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var fetalMovementCount by remember { mutableStateOf("") }
    var fetalMovementPeriod by remember { mutableStateOf("") }
    var fetalMovementFeeling by remember { mutableStateOf("") }
    var symptomType by remember { mutableStateOf("") }
    var symptomSeverity by remember { mutableStateOf("") }
    var exerciseType by remember { mutableStateOf("") }
    var exerciseMinutes by remember { mutableStateOf("") }
    var exerciseIntensity by remember { mutableStateOf("") }
    var dietMeal by remember { mutableStateOf("") }
    var dietContent by remember { mutableStateOf("") }
    var appointmentTime by remember { mutableStateOf("") }
    var appointmentLocation by remember { mutableStateOf("") }
    var appointmentDoctor by remember { mutableStateOf("") }
    var appointmentItems by remember { mutableStateOf("") }
    var appointmentResult by remember { mutableStateOf("") }
    var editingRecord by remember { mutableStateOf<CalendarRecord?>(null) }
    var recordFilter by remember { mutableStateOf<CalendarRecordType?>(null) }
    var errorMessageResId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val clearForm = {
        clearRecordForm(
            onNoteChange = { note = it },
            onWeightKgChange = { weightKg = it },
            onFetalMovementCountChange = { fetalMovementCount = it },
            onFetalMovementPeriodChange = { fetalMovementPeriod = it },
            onFetalMovementFeelingChange = { fetalMovementFeeling = it },
            onSymptomTypeChange = { symptomType = it },
            onSymptomSeverityChange = { symptomSeverity = it },
            onExerciseTypeChange = { exerciseType = it },
            onExerciseMinutesChange = { exerciseMinutes = it },
            onExerciseIntensityChange = { exerciseIntensity = it },
            onDietMealChange = { dietMeal = it },
            onDietContentChange = { dietContent = it },
            onAppointmentTimeChange = { appointmentTime = it },
            onAppointmentLocationChange = { appointmentLocation = it },
            onAppointmentDoctorChange = { appointmentDoctor = it },
            onAppointmentItemsChange = { appointmentItems = it },
            onAppointmentResultChange = { appointmentResult = it },
        )
    }
    val filteredRecords = remember(uiState.records, recordFilter) {
        recordFilter?.let { type -> uiState.records.filter { it.type == type } } ?: uiState.records
    }

    LaunchedEffect(initialRecordType) {
        val quickRecordType = initialRecordType ?: return@LaunchedEffect
        recordType = quickRecordType
        editingRecord = null
        errorMessageResId = null
        clearForm()
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
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        MonthCalendar(
            uiState = uiState,
            onDateSelected = onSelectCalendarDate,
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
            fetalMovementPeriod = fetalMovementPeriod,
            onFetalMovementPeriodChange = { fetalMovementPeriod = it },
            fetalMovementFeeling = fetalMovementFeeling,
            onFetalMovementFeelingChange = { fetalMovementFeeling = it },
            symptomType = symptomType,
            onSymptomTypeChange = { symptomType = it },
            symptomSeverity = symptomSeverity,
            onSymptomSeverityChange = { symptomSeverity = it },
            exerciseType = exerciseType,
            onExerciseTypeChange = { exerciseType = it },
            exerciseMinutes = exerciseMinutes,
            onExerciseMinutesChange = { exerciseMinutes = it },
            exerciseIntensity = exerciseIntensity,
            onExerciseIntensityChange = { exerciseIntensity = it },
            dietMeal = dietMeal,
            onDietMealChange = { dietMeal = it },
            dietContent = dietContent,
            onDietContentChange = { dietContent = it },
            appointmentTime = appointmentTime,
            onAppointmentTimeChange = { appointmentTime = it },
            appointmentLocation = appointmentLocation,
            onAppointmentLocationChange = { appointmentLocation = it },
            appointmentDoctor = appointmentDoctor,
            onAppointmentDoctorChange = { appointmentDoctor = it },
            appointmentItems = appointmentItems,
            onAppointmentItemsChange = { appointmentItems = it },
            appointmentResult = appointmentResult,
            onAppointmentResultChange = { appointmentResult = it },
            editing = editingRecord != null,
            onCancelEdit = {
                editingRecord = null
                clearForm()
            },
            onSave = {
                coroutineScope.launch {
                    val result = onSaveRecord(
                        CalendarRecordInput(
                            id = editingRecord?.id,
                            date = selectedDateText,
                            type = recordType,
                            note = note,
                            weightKg = weightKg,
                            fetalMovementCount = fetalMovementCount,
                            fetalMovementPeriod = fetalMovementPeriod,
                            fetalMovementFeeling = fetalMovementFeeling,
                            symptomType = symptomType,
                            symptomSeverity = symptomSeverity,
                            exerciseType = exerciseType,
                            exerciseIntensity = exerciseIntensity,
                            dietMeal = dietMeal,
                            dietContent = dietContent,
                            appointmentTime = appointmentTime,
                            appointmentLocation = appointmentLocation,
                            appointmentDoctor = appointmentDoctor,
                            appointmentItems = appointmentItems,
                            appointmentResult = appointmentResult,
                            exerciseMinutes = exerciseMinutes,
                            createdAtEpochMillis = editingRecord?.createdAtEpochMillis,
                        ),
                    )
                    errorMessageResId = result.toErrorMessageResId()
                    if (result == CalendarRecordActionResult.Success) {
                        editingRecord = null
                        clearForm()
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
            records = filteredRecords,
            hasRecordsForDate = uiState.records.isNotEmpty(),
            recordFilter = recordFilter,
            onRecordFilterChange = { recordFilter = it },
            onEditRecord = { record ->
                editingRecord = record
                selectedDateText = record.date.toString()
                recordType = record.type
                note = record.note
                weightKg = record.weightKg?.toString().orEmpty()
                fetalMovementCount = record.fetalMovementCount?.toString().orEmpty()
                fetalMovementPeriod = record.fetalMovementPeriod.orEmpty()
                fetalMovementFeeling = record.fetalMovementFeeling.orEmpty()
                symptomType = record.symptomType.orEmpty()
                symptomSeverity = record.symptomSeverity.orEmpty()
                exerciseType = record.exerciseType.orEmpty()
                exerciseMinutes = record.exerciseMinutes?.toString().orEmpty()
                exerciseIntensity = record.exerciseIntensity.orEmpty()
                dietMeal = record.dietMeal.orEmpty()
                dietContent = record.dietContent.orEmpty()
                appointmentTime = record.appointmentTime.orEmpty()
                appointmentLocation = record.appointmentLocation.orEmpty()
                appointmentDoctor = record.appointmentDoctor.orEmpty()
                appointmentItems = record.appointmentItems.orEmpty()
                appointmentResult = record.appointmentResult.orEmpty()
                errorMessageResId = null
            },
            onDeleteRecord = onDeleteRecord,
        )
    }
}

@Composable
private fun MonthCalendar(
    uiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onDateSelected(uiState.selectedDate.minusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.calendar_previous_month),
                    )
                }
                Text(
                    text = uiState.displayedMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                )
                IconButton(onClick = { onDateSelected(uiState.selectedDate.plusMonths(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.calendar_next_month),
                    )
                }
            }
            CalendarWeekHeader()
            uiState.monthDays.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    week.forEach { day ->
                        CalendarDayCell(
                            day = day,
                            selected = day.date == uiState.selectedDate,
                            onClick = { onDateSelected(day.date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    val labels = listOf(
        R.string.calendar_week_monday,
        R.string.calendar_week_tuesday,
        R.string.calendar_week_wednesday,
        R.string.calendar_week_thursday,
        R.string.calendar_week_friday,
        R.string.calendar_week_saturday,
        R.string.calendar_week_sunday,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        labels.forEach { labelResId ->
            Text(
                text = stringResource(labelResId),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarMonthDay,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor = if (selected) colorScheme.primaryContainer else Color.Transparent
    val textColor = when {
        selected -> colorScheme.onPrimaryContainer
        day.isInDisplayedMonth -> colorScheme.onSurface
        else -> colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
        )
        RecordTypeDots(recordTypes = day.recordTypes)
    }
}

@Composable
private fun RecordTypeDots(recordTypes: Set<CalendarRecordType>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        recordTypes.take(4).forEach { type ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(type.toMarkerColor()),
            )
        }
    }
}

@Composable
private fun CalendarRecordType.toMarkerColor(): Color = when (this) {
    CalendarRecordType.Appointment -> MaterialTheme.colorScheme.primary
    CalendarRecordType.Weight -> MaterialTheme.colorScheme.tertiary
    CalendarRecordType.FetalMovement -> MaterialTheme.colorScheme.secondary
    CalendarRecordType.Symptom -> MaterialTheme.colorScheme.error
    CalendarRecordType.Exercise -> MaterialTheme.colorScheme.inversePrimary
    CalendarRecordType.Diet -> MaterialTheme.colorScheme.primaryContainer
    CalendarRecordType.Note -> MaterialTheme.colorScheme.outline
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
                keyboardOptions = isoDateKeyboardOptions(),
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
    fetalMovementPeriod: String,
    onFetalMovementPeriodChange: (String) -> Unit,
    fetalMovementFeeling: String,
    onFetalMovementFeelingChange: (String) -> Unit,
    symptomType: String,
    onSymptomTypeChange: (String) -> Unit,
    symptomSeverity: String,
    onSymptomSeverityChange: (String) -> Unit,
    exerciseType: String,
    onExerciseTypeChange: (String) -> Unit,
    exerciseMinutes: String,
    onExerciseMinutesChange: (String) -> Unit,
    exerciseIntensity: String,
    onExerciseIntensityChange: (String) -> Unit,
    dietMeal: String,
    onDietMealChange: (String) -> Unit,
    dietContent: String,
    onDietContentChange: (String) -> Unit,
    appointmentTime: String,
    onAppointmentTimeChange: (String) -> Unit,
    appointmentLocation: String,
    onAppointmentLocationChange: (String) -> Unit,
    appointmentDoctor: String,
    onAppointmentDoctorChange: (String) -> Unit,
    appointmentItems: String,
    onAppointmentItemsChange: (String) -> Unit,
    appointmentResult: String,
    onAppointmentResultChange: (String) -> Unit,
    editing: Boolean,
    onCancelEdit: () -> Unit,
    onSave: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(
                    if (editing) R.string.calendar_edit_record else R.string.calendar_add_record,
                ),
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
                    OutlinedTextField(
                        value = appointmentDoctor,
                        onValueChange = onAppointmentDoctorChange,
                        label = { Text(stringResource(R.string.calendar_appointment_doctor_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = appointmentItems,
                        onValueChange = onAppointmentItemsChange,
                        label = { Text(stringResource(R.string.calendar_appointment_items_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                    OutlinedTextField(
                        value = appointmentResult,
                        onValueChange = onAppointmentResultChange,
                        label = { Text(stringResource(R.string.calendar_appointment_result_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
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

                CalendarRecordType.FetalMovement -> {
                    OutlinedTextField(
                        value = fetalMovementCount,
                        onValueChange = onFetalMovementCountChange,
                        label = { Text(stringResource(R.string.calendar_fetal_movement_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = fetalMovementPeriod,
                        onValueChange = onFetalMovementPeriodChange,
                        label = { Text(stringResource(R.string.calendar_fetal_movement_period_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = fetalMovementFeeling,
                        onValueChange = onFetalMovementFeelingChange,
                        label = { Text(stringResource(R.string.calendar_fetal_movement_feeling_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                }

                CalendarRecordType.Symptom -> {
                    OutlinedTextField(
                        value = symptomType,
                        onValueChange = onSymptomTypeChange,
                        label = { Text(stringResource(R.string.calendar_symptom_type_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = symptomSeverity,
                        onValueChange = onSymptomSeverityChange,
                        label = { Text(stringResource(R.string.calendar_symptom_severity_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                CalendarRecordType.Exercise -> {
                    OutlinedTextField(
                        value = exerciseType,
                        onValueChange = onExerciseTypeChange,
                        label = { Text(stringResource(R.string.calendar_exercise_type_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = exerciseMinutes,
                        onValueChange = onExerciseMinutesChange,
                        label = { Text(stringResource(R.string.calendar_exercise_minutes_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = exerciseIntensity,
                        onValueChange = onExerciseIntensityChange,
                        label = { Text(stringResource(R.string.calendar_exercise_intensity_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                CalendarRecordType.Diet -> {
                    OutlinedTextField(
                        value = dietMeal,
                        onValueChange = onDietMealChange,
                        label = { Text(stringResource(R.string.calendar_diet_meal_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = dietContent,
                        onValueChange = onDietContentChange,
                        label = { Text(stringResource(R.string.calendar_diet_content_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                }

                CalendarRecordType.Note -> Unit
            }

            if (recordType.shouldShowMedicalAttentionNotice()) {
                Text(
                    text = stringResource(R.string.calendar_medical_attention_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
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
                Text(
                    stringResource(
                        if (editing) R.string.calendar_update_record else R.string.calendar_save_record,
                    ),
                )
            }
            if (editing) {
                OutlinedButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.calendar_cancel_edit))
                }
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
    hasRecordsForDate: Boolean,
    recordFilter: CalendarRecordType?,
    onRecordFilterChange: (CalendarRecordType?) -> Unit,
    onEditRecord: (CalendarRecord) -> Unit,
    onDeleteRecord: suspend (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = stringResource(R.string.calendar_selected_date_records),
        style = MaterialTheme.typography.titleMedium,
    )
    RecordFilterSelector(
        recordFilter = recordFilter,
        onRecordFilterChange = onRecordFilterChange,
    )
    if (records.isEmpty()) {
        Text(
            text = stringResource(
                if (hasRecordsForDate) {
                    R.string.calendar_empty_filtered_records
                } else {
                    R.string.calendar_empty_records
                },
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        records.forEach { record ->
            RecordCard(
                record = record,
                onEdit = { onEditRecord(record) },
                onDelete = {
                    coroutineScope.launch { onDeleteRecord(record.id) }
                },
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RecordFilterSelector(
    recordFilter: CalendarRecordType?,
    onRecordFilterChange: (CalendarRecordType?) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = recordFilter == null,
            onClick = { onRecordFilterChange(null) },
            label = { Text(stringResource(R.string.calendar_filter_all)) },
        )
        CalendarRecordType.entries.forEach { type ->
            FilterChip(
                selected = recordFilter == type,
                onClick = { onRecordFilterChange(type) },
                label = { Text(type.toDisplayText()) },
            )
        }
    }
}

@Composable
private fun RecordCard(
    record: CalendarRecord,
    onEdit: () -> Unit,
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEdit) {
                        Text(stringResource(R.string.calendar_edit_record))
                    }
                    Button(onClick = { showDeleteConfirm = true }) {
                        Text(stringResource(R.string.calendar_delete_record))
                    }
                }
            }
            record.weightKg?.let {
                Text(stringResource(R.string.calendar_record_weight_value, it))
            }
            record.fetalMovementCount?.let {
                Text(stringResource(R.string.calendar_record_fetal_movement_value, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.fetalMovementPeriod)?.let {
                Text(stringResource(R.string.calendar_record_fetal_movement_period, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.fetalMovementFeeling)?.let {
                Text(stringResource(R.string.calendar_record_fetal_movement_feeling, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.symptomType)?.let {
                Text(stringResource(R.string.calendar_record_symptom_type, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.symptomSeverity)?.let {
                Text(stringResource(R.string.calendar_record_symptom_severity, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.exerciseType)?.let {
                Text(stringResource(R.string.calendar_record_exercise_type, it))
            }
            record.exerciseMinutes?.let {
                Text(stringResource(R.string.calendar_record_exercise_value, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.exerciseIntensity)?.let {
                Text(stringResource(R.string.calendar_record_exercise_intensity, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.dietMeal)?.let {
                Text(stringResource(R.string.calendar_record_diet_meal, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.dietContent)?.let {
                Text(stringResource(R.string.calendar_record_diet_content, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.appointmentTime)?.let {
                Text(stringResource(R.string.calendar_record_appointment_time, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.appointmentLocation)?.let {
                Text(stringResource(R.string.calendar_record_appointment_location, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.appointmentDoctor)?.let {
                Text(stringResource(R.string.calendar_record_appointment_doctor, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.appointmentItems)?.let {
                Text(stringResource(R.string.calendar_record_appointment_items, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.appointmentResult)?.let {
                Text(stringResource(R.string.calendar_record_appointment_result, it))
            }
            CalendarRecordDisplayText.visibleOrNull(record.note)?.let {
                Text(it)
            }
            if (record.type.shouldShowMedicalAttentionNotice()) {
                Text(
                    text = stringResource(R.string.calendar_medical_attention_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun CalendarRecordType.toDisplayText(): String = when (this) {
    CalendarRecordType.Appointment -> stringResource(R.string.calendar_record_type_appointment)
    CalendarRecordType.Weight -> stringResource(R.string.calendar_record_type_weight)
    CalendarRecordType.FetalMovement -> stringResource(R.string.calendar_record_type_fetal_movement)
    CalendarRecordType.Symptom -> stringResource(R.string.calendar_record_type_symptom)
    CalendarRecordType.Exercise -> stringResource(R.string.calendar_record_type_exercise)
    CalendarRecordType.Diet -> stringResource(R.string.calendar_record_type_diet)
    CalendarRecordType.Note -> stringResource(R.string.calendar_record_type_note)
}

private fun CalendarRecordActionResult.toErrorMessageResId(): Int? = when (this) {
    CalendarRecordActionResult.Success -> null
    CalendarRecordActionResult.InvalidDate -> R.string.calendar_error_invalid_date
    CalendarRecordActionResult.InvalidWeight -> R.string.calendar_error_invalid_weight
    CalendarRecordActionResult.InvalidFetalMovement -> R.string.calendar_error_invalid_fetal_movement
    CalendarRecordActionResult.InvalidExerciseMinutes -> R.string.calendar_error_invalid_exercise_minutes
    CalendarRecordActionResult.InvalidAppointmentTime -> R.string.calendar_error_invalid_appointment_time
    CalendarRecordActionResult.InvalidRecordContent -> R.string.calendar_error_empty_record_content
}

private fun CalendarRecordType.shouldShowMedicalAttentionNotice(): Boolean = when (this) {
    CalendarRecordType.FetalMovement,
    CalendarRecordType.Symptom -> true

    CalendarRecordType.Appointment,
    CalendarRecordType.Weight,
    CalendarRecordType.Exercise,
    CalendarRecordType.Diet,
    CalendarRecordType.Note -> false
}

private fun clearRecordForm(
    onNoteChange: (String) -> Unit,
    onWeightKgChange: (String) -> Unit,
    onFetalMovementCountChange: (String) -> Unit,
    onFetalMovementPeriodChange: (String) -> Unit,
    onFetalMovementFeelingChange: (String) -> Unit,
    onSymptomTypeChange: (String) -> Unit,
    onSymptomSeverityChange: (String) -> Unit,
    onExerciseTypeChange: (String) -> Unit,
    onExerciseMinutesChange: (String) -> Unit,
    onExerciseIntensityChange: (String) -> Unit,
    onDietMealChange: (String) -> Unit,
    onDietContentChange: (String) -> Unit,
    onAppointmentTimeChange: (String) -> Unit,
    onAppointmentLocationChange: (String) -> Unit,
    onAppointmentDoctorChange: (String) -> Unit,
    onAppointmentItemsChange: (String) -> Unit,
    onAppointmentResultChange: (String) -> Unit,
) {
    onNoteChange("")
    onWeightKgChange("")
    onFetalMovementCountChange("")
    onFetalMovementPeriodChange("")
    onFetalMovementFeelingChange("")
    onSymptomTypeChange("")
    onSymptomSeverityChange("")
    onExerciseTypeChange("")
    onExerciseMinutesChange("")
    onExerciseIntensityChange("")
    onDietMealChange("")
    onDietContentChange("")
    onAppointmentTimeChange("")
    onAppointmentLocationChange("")
    onAppointmentDoctorChange("")
    onAppointmentItemsChange("")
    onAppointmentResultChange("")
}

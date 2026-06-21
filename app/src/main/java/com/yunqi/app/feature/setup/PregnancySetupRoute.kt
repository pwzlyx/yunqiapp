package com.yunqi.app.feature.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import kotlinx.coroutines.launch

@Composable
fun PregnancySetupRoute(
    contentPadding: PaddingValues,
    onProfileSaved: () -> Unit,
    viewModel: PregnancySetupViewModel = viewModel(),
) {
    var selectedMethod by remember { mutableStateOf(SetupMethod.LastMenstrualPeriod) }
    var lmpDate by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var week by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
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
            text = stringResource(R.string.setup_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.setup_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        MethodSelector(
            selectedMethod = selectedMethod,
            onMethodSelected = {
                selectedMethod = it
                errorMessageResId = null
            },
        )

        when (selectedMethod) {
            SetupMethod.LastMenstrualPeriod -> {
                DateField(
                    value = lmpDate,
                    onValueChange = { lmpDate = it },
                    label = stringResource(R.string.setup_lmp_label),
                )
            }

            SetupMethod.DueDate -> {
                DateField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = stringResource(R.string.setup_due_date_label),
                )
            }

            SetupMethod.CurrentGestationalAge -> {
                OutlinedTextField(
                    value = week,
                    onValueChange = { week = it },
                    label = { Text(stringResource(R.string.setup_week_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it },
                    label = { Text(stringResource(R.string.setup_day_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        errorMessageResId?.let { messageResId ->
            Text(
                text = stringResource(messageResId),
                color = MaterialTheme.colorScheme.error,
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    val result = when (selectedMethod) {
                        SetupMethod.LastMenstrualPeriod -> viewModel.saveLastMenstrualPeriod(lmpDate)
                        SetupMethod.DueDate -> viewModel.saveDueDate(dueDate)
                        SetupMethod.CurrentGestationalAge -> viewModel.saveCurrentGestationalAge(week, day)
                    }

                    errorMessageResId = result.toErrorMessageResId()
                    if (result == SaveProfileResult.Success) {
                        onProfileSaved()
                    }
                }
            },
        ) {
            Text(stringResource(R.string.setup_save))
        }

        Text(
            text = stringResource(R.string.medical_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MethodSelector(
    selectedMethod: SetupMethod,
    onMethodSelected: (SetupMethod) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selectedMethod == SetupMethod.LastMenstrualPeriod,
            onClick = { onMethodSelected(SetupMethod.LastMenstrualPeriod) },
            label = { Text(stringResource(R.string.setup_method_lmp)) },
        )
        FilterChip(
            selected = selectedMethod == SetupMethod.DueDate,
            onClick = { onMethodSelected(SetupMethod.DueDate) },
            label = { Text(stringResource(R.string.setup_method_due_date)) },
        )
        FilterChip(
            selected = selectedMethod == SetupMethod.CurrentGestationalAge,
            onClick = { onMethodSelected(SetupMethod.CurrentGestationalAge) },
            label = { Text(stringResource(R.string.setup_method_current_age)) },
        )
    }
}

@Composable
private fun DateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.setup_date_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun SaveProfileResult.toErrorMessageResId(): Int? = when (this) {
    SaveProfileResult.Success -> null
    SaveProfileResult.InvalidDate -> R.string.setup_error_invalid_date
    SaveProfileResult.InvalidWeek -> R.string.setup_error_invalid_week
    SaveProfileResult.InvalidDay -> R.string.setup_error_invalid_day
}


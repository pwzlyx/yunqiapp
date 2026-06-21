package com.yunqi.app.feature.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.yunqi.app.domain.pregnancy.Trimester
import com.yunqi.app.domain.pregnancy.calculateProgress
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun PregnancySetupRoute(
    contentPadding: PaddingValues,
    onProfileSaved: () -> Unit,
    viewModel: PregnancySetupViewModel = viewModel(),
) {
    var selectedMethod by remember { mutableStateOf(SetupMethod.LastMenstrualPeriod) }
    var lmpDate by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var conceptionDate by remember { mutableStateOf("") }
    var week by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var errorMessageResId by remember { mutableStateOf<Int?>(null) }
    val parser = remember { PregnancyProfileFormParser() }
    val coroutineScope = rememberCoroutineScope()
    val input = PregnancySetupInput(
        method = selectedMethod,
        lmpDate = lmpDate,
        dueDate = dueDate,
        conceptionDate = conceptionDate,
        week = week,
        day = day,
    )
    val previewResult = remember(input) { parser.parse(input) }

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

            SetupMethod.ConceptionDate -> {
                DateField(
                    value = conceptionDate,
                    onValueChange = { conceptionDate = it },
                    label = stringResource(R.string.setup_conception_date_label),
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

        PreviewCard(parseResult = previewResult)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    val result = viewModel.save(input)

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
private fun PreviewCard(parseResult: PregnancyProfileParseResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.setup_preview_title),
                style = MaterialTheme.typography.titleMedium,
            )

            if (parseResult is PregnancyProfileParseResult.Success) {
                val progress = parseResult.profile.calculateProgress(today = LocalDate.now())
                Text(
                    text = stringResource(
                        R.string.setup_preview_gestational_age,
                        progress.week,
                        progress.day,
                    ),
                )
                Text(
                    text = stringResource(
                        R.string.setup_preview_due_date,
                        progress.dueDate.toString(),
                    ),
                )
                Text(
                    text = stringResource(
                        R.string.setup_preview_trimester,
                        progress.trimester.toDisplayText(),
                    ),
                )
            } else {
                Text(
                    text = stringResource(R.string.setup_preview_waiting),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MethodSelector(
    selectedMethod: SetupMethod,
    onMethodSelected: (SetupMethod) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
            selected = selectedMethod == SetupMethod.ConceptionDate,
            onClick = { onMethodSelected(SetupMethod.ConceptionDate) },
            label = { Text(stringResource(R.string.setup_method_conception_date)) },
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

@Composable
private fun Trimester.toDisplayText(): String = when (this) {
    Trimester.First -> stringResource(R.string.trimester_first)
    Trimester.Second -> stringResource(R.string.trimester_second)
    Trimester.Third -> stringResource(R.string.trimester_third)
    Trimester.PostDue -> stringResource(R.string.trimester_post_due)
}

private fun SaveProfileResult.toErrorMessageResId(): Int? = when (this) {
    SaveProfileResult.Success -> null
    SaveProfileResult.InvalidDate -> R.string.setup_error_invalid_date
    SaveProfileResult.InvalidWeek -> R.string.setup_error_invalid_week
    SaveProfileResult.InvalidDay -> R.string.setup_error_invalid_day
}

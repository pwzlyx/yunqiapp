package com.yunqi.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R

@Composable
fun HomeRoute(
    contentPadding: PaddingValues,
    viewModel: HomeViewModel = viewModel(),
) {
    HomeScreen(
        contentPadding = contentPadding,
        uiState = viewModel.uiState,
    )
}

@Composable
private fun HomeScreen(
    contentPadding: PaddingValues,
    uiState: HomeUiState,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (uiState) {
            HomeUiState.ProfileMissing -> {
                item { MissingProfileCard() }
                item { PlanningCard() }
            }

            is HomeUiState.Ready -> {
                item { PregnancyProgressHeader(uiState) }
                item { ReminderCard(uiState.reminders) }
                item { PlanningCard() }
            }
        }

        item {
            Text(
                text = stringResource(R.string.medical_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PregnancyProgressHeader(uiState: HomeUiState.Ready) {
    Text(
        text = stringResource(R.string.home_today),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
        text = stringResource(
            R.string.home_gestational_age,
            uiState.progress.week,
            uiState.progress.day,
        ),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
    )
    Text(
        text = stringResource(
            R.string.home_days_until_due_date,
            uiState.progress.daysUntilDueDate,
        ),
        style = MaterialTheme.typography.bodyLarge,
    )
}

@Composable
private fun MissingProfileCard() {
    SectionCard(title = stringResource(R.string.home_profile_missing_title)) {
        Text(
            text = stringResource(R.string.home_profile_missing_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = {}) {
            Text(stringResource(R.string.home_profile_missing_action))
        }
    }
}

@Composable
private fun ReminderCard(reminders: List<String>) {
    SectionCard(title = stringResource(R.string.home_section_reminders)) {
        if (reminders.isEmpty()) {
            Text(
                text = stringResource(R.string.home_empty_reminders),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            reminders.forEach { reminder ->
                AssistChip(onClick = {}, label = { Text(reminder) })
            }
        }
    }
}

@Composable
private fun PlanningCard() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionCard(title = stringResource(R.string.home_section_diet)) {
            Text(stringResource(R.string.home_diet_placeholder))
        }
        SectionCard(title = stringResource(R.string.home_section_exercise)) {
            Text(stringResource(R.string.home_exercise_placeholder))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            content()
        }
    }
}

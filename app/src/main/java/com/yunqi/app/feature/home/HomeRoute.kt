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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import com.yunqi.app.data.content.PregnancyContentCard
import com.yunqi.app.data.content.PregnancyContentCategory
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod

@Composable
fun HomeRoute(
    contentPadding: PaddingValues,
    onSetProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onSetProfileClick = onSetProfileClick,
    )
}

@Composable
private fun HomeScreen(
    contentPadding: PaddingValues,
    uiState: HomeUiState,
    onSetProfileClick: () -> Unit,
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
                item { MissingProfileCard(onSetProfileClick = onSetProfileClick) }
            }

            is HomeUiState.Ready -> {
                item { PregnancyProgressHeader(uiState) }
                item { ProfileSourceCard(uiState.calculationMethod) }
                item { ReminderCard(contentCards = uiState.contentCards) }
                item { PlanningCard(contentCards = uiState.contentCards) }
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
private fun MissingProfileCard(onSetProfileClick: () -> Unit) {
    SectionCard(title = stringResource(R.string.home_profile_missing_title)) {
        Text(
            text = stringResource(R.string.home_profile_missing_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onSetProfileClick) {
            Text(stringResource(R.string.home_profile_missing_action))
        }
    }
}

@Composable
private fun ProfileSourceCard(calculationMethod: PregnancyCalculationMethod) {
    val sourceText = when (calculationMethod) {
        PregnancyCalculationMethod.LastMenstrualPeriod -> stringResource(R.string.home_profile_source_lmp)
        PregnancyCalculationMethod.DueDate -> stringResource(R.string.home_profile_source_due_date)
        PregnancyCalculationMethod.ConceptionDate -> stringResource(R.string.home_profile_source_conception_date)
        PregnancyCalculationMethod.CurrentGestationalAge -> stringResource(R.string.home_profile_source_current_age)
    }

    SectionCard(title = sourceText) {
        Text(
            text = stringResource(R.string.home_profile_saved_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReminderCard(contentCards: List<PregnancyContentCard>) {
    val safetyCard = contentCards.firstOrNull { it.category == PregnancyContentCategory.Safety }

    SectionCard(title = stringResource(R.string.home_section_reminders)) {
        AssistChip(onClick = {}, label = { Text(stringResource(R.string.home_reminder_weight)) })
        AssistChip(onClick = {}, label = { Text(stringResource(R.string.home_reminder_fetal_movement)) })
        AssistChip(onClick = {}, label = { Text(stringResource(R.string.home_reminder_appointment)) })
        safetyCard?.let { card ->
            Text(
                text = stringResource(card.titleResId),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(card.bodyResId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PlanningCard(contentCards: List<PregnancyContentCard>) {
    val cardsByCategory = contentCards.groupBy { it.category }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContentSection(
            title = stringResource(R.string.home_section_diet),
            cards = cardsByCategory[PregnancyContentCategory.Diet].orEmpty(),
        )
        ContentSection(
            title = stringResource(R.string.home_section_exercise),
            cards = cardsByCategory[PregnancyContentCategory.Exercise].orEmpty(),
        )
        ContentSection(
            title = stringResource(R.string.home_section_safety),
            cards = cardsByCategory[PregnancyContentCategory.Safety].orEmpty(),
        )
    }
}

@Composable
private fun ContentSection(
    title: String,
    cards: List<PregnancyContentCard>,
) {
    SectionCard(title = title) {
        cards.forEach { card ->
            Text(
                text = stringResource(card.titleResId),
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = stringResource(card.bodyResId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    R.string.home_content_source,
                    stringResource(card.sourceNameResId),
                    card.reviewedAt,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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

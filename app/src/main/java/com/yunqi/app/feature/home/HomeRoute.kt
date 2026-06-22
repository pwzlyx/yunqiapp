package com.yunqi.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.yunqi.app.data.local.ContentStatus
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod

@Composable
fun HomeRoute(
    contentPadding: PaddingValues,
    onSetProfileClick: () -> Unit,
    onQuickRecordClick: (CalendarRecordType) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        onSetProfileClick = onSetProfileClick,
        onQuickRecordClick = onQuickRecordClick,
        onToggleContentRead = viewModel::toggleContentRead,
        onToggleContentFavorite = viewModel::toggleContentFavorite,
    )
}

@Composable
private fun HomeScreen(
    contentPadding: PaddingValues,
    uiState: HomeUiState,
    onSetProfileClick: () -> Unit,
    onQuickRecordClick: (CalendarRecordType) -> Unit,
    onToggleContentRead: (String) -> Unit,
    onToggleContentFavorite: (String) -> Unit,
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
                item {
                    ReminderCard(
                        contentCards = uiState.contentCards,
                        onQuickRecordClick = onQuickRecordClick,
                    )
                }
                item {
                    PlanningCard(
                        contentCards = uiState.contentCards,
                        contentStatus = uiState.contentStatus,
                        exerciseRestricted = uiState.exerciseRestricted,
                        onToggleContentRead = onToggleContentRead,
                        onToggleContentFavorite = onToggleContentFavorite,
                    )
                }
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
private fun ReminderCard(
    contentCards: List<PregnancyContentCard>,
    onQuickRecordClick: (CalendarRecordType) -> Unit,
) {
    val safetyCard = contentCards.firstOrNull { it.category == PregnancyContentCategory.Safety }

    SectionCard(title = stringResource(R.string.home_section_reminders)) {
        AssistChip(
            onClick = { onQuickRecordClick(CalendarRecordType.Weight) },
            label = { Text(stringResource(R.string.home_reminder_weight)) },
        )
        AssistChip(
            onClick = { onQuickRecordClick(CalendarRecordType.FetalMovement) },
            label = { Text(stringResource(R.string.home_reminder_fetal_movement)) },
        )
        AssistChip(
            onClick = { onQuickRecordClick(CalendarRecordType.Symptom) },
            label = { Text(stringResource(R.string.home_reminder_symptom)) },
        )
        AssistChip(
            onClick = { onQuickRecordClick(CalendarRecordType.Appointment) },
            label = { Text(stringResource(R.string.home_reminder_appointment)) },
        )
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
private fun PlanningCard(
    contentCards: List<PregnancyContentCard>,
    contentStatus: ContentStatus,
    exerciseRestricted: Boolean,
    onToggleContentRead: (String) -> Unit,
    onToggleContentFavorite: (String) -> Unit,
) {
    val cardsByCategory = contentCards.groupBy { it.category }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContentSection(
            title = stringResource(R.string.home_section_diet),
            cards = cardsByCategory[PregnancyContentCategory.Diet].orEmpty(),
            readContentIds = contentStatus.readContentIds,
            favoriteContentIds = contentStatus.favoriteContentIds,
            onToggleRead = onToggleContentRead,
            onToggleFavorite = onToggleContentFavorite,
        )
        ContentSection(
            title = stringResource(R.string.home_section_antenatal_care),
            cards = cardsByCategory[PregnancyContentCategory.AntenatalCare].orEmpty(),
        )
        if (exerciseRestricted) {
            SectionCard(title = stringResource(R.string.home_section_exercise)) {
                Text(
                    text = stringResource(R.string.home_exercise_restricted_body),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            ContentSection(
                title = stringResource(R.string.home_section_exercise),
                cards = cardsByCategory[PregnancyContentCategory.Exercise].orEmpty(),
            )
        }
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
    readContentIds: Set<String> = emptySet(),
    favoriteContentIds: Set<String> = emptySet(),
    onToggleRead: ((String) -> Unit)? = null,
    onToggleFavorite: ((String) -> Unit)? = null,
) {
    SectionCard(title = title) {
        cards.forEach { card ->
            val isRead = card.id in readContentIds
            val isFavorite = card.id in favoriteContentIds
            Text(
                text = stringResource(card.titleResId),
                style = MaterialTheme.typography.titleSmall,
            )
            if (onToggleRead != null || onToggleFavorite != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    onToggleRead?.let { toggleRead ->
                        AssistChip(
                            onClick = { toggleRead(card.id) },
                            label = {
                                Text(
                                    stringResource(
                                        if (isRead) {
                                            R.string.home_content_mark_unread
                                        } else {
                                            R.string.home_content_mark_read
                                        },
                                    ),
                                )
                            },
                        )
                    }
                    onToggleFavorite?.let { toggleFavorite ->
                        AssistChip(
                            onClick = { toggleFavorite(card.id) },
                            label = {
                                Text(
                                    stringResource(
                                        if (isFavorite) {
                                            R.string.home_content_remove_favorite
                                        } else {
                                            R.string.home_content_add_favorite
                                        },
                                    ),
                                )
                            },
                        )
                    }
                }
            }
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
            Text(
                text = stringResource(R.string.home_content_source_url, card.sourceUrl),
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

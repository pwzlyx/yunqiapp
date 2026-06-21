package com.yunqi.app.feature.trends

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.yunqi.app.domain.trends.TrendSummary

@Composable
fun TrendsRoute(
    contentPadding: PaddingValues,
    viewModel: TrendsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrendsScreen(
        contentPadding = contentPadding,
        summary = uiState.summary,
    )
}

@Composable
private fun TrendsScreen(
    contentPadding: PaddingValues,
    summary: TrendSummary,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.trends_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        TrendCard(
            titleResId = R.string.trends_weight_title,
            body = summary.latestWeightKg?.let {
                stringResource(
                    R.string.trends_weight_summary,
                    summary.weightRecordCount,
                    it,
                )
            } ?: stringResource(R.string.trends_weight_body),
        )
        TrendCard(
            titleResId = R.string.trends_fetal_movement_title,
            body = summary.latestFetalMovementCount?.let {
                stringResource(
                    R.string.trends_fetal_movement_summary,
                    summary.fetalMovementRecordCount,
                    it,
                )
            } ?: stringResource(R.string.trends_fetal_movement_body),
        )
        TrendCard(
            titleResId = R.string.trends_exercise_title,
            body = stringResource(R.string.trends_exercise_body),
        )
    }
}

@Composable
private fun TrendCard(
    @StringRes titleResId: Int,
    body: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


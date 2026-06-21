package com.yunqi.app.feature.trends

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yunqi.app.R
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.domain.trends.TrendRange
import com.yunqi.app.domain.trends.TrendPoint
import com.yunqi.app.domain.trends.TrendSummary
import kotlin.math.max

@Composable
fun TrendsRoute(
    contentPadding: PaddingValues,
    onRecordClick: (CalendarRecordType) -> Unit,
    viewModel: TrendsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrendsScreen(
        contentPadding = contentPadding,
        selectedRange = uiState.selectedRange,
        summary = uiState.summary,
        onRangeSelected = viewModel::selectRange,
        onRecordClick = onRecordClick,
    )
}

@Composable
private fun TrendsScreen(
    contentPadding: PaddingValues,
    selectedRange: TrendRange,
    summary: TrendSummary,
    onRangeSelected: (TrendRange) -> Unit,
    onRecordClick: (CalendarRecordType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.trends_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        TrendRangeSelector(
            selectedRange = selectedRange,
            onRangeSelected = onRangeSelected,
        )
        TrendCard(
            titleResId = R.string.trends_weight_title,
            body = summary.latestWeightKg?.let {
                stringResource(
                    R.string.trends_weight_summary_with_change,
                    summary.weightRecordCount,
                    it,
                    summary.weightChangeKg ?: 0.0,
                )
            } ?: stringResource(R.string.trends_weight_body),
            points = summary.weightPoints,
            emptyActionResId = R.string.trends_record_weight,
            recordType = CalendarRecordType.Weight,
            onRecordClick = onRecordClick,
        )
        TrendCard(
            titleResId = R.string.trends_fetal_movement_title,
            body = summary.latestFetalMovementCount?.let {
                stringResource(
                    R.string.trends_fetal_movement_summary_with_average,
                    summary.fetalMovementRecordCount,
                    it,
                    summary.averageFetalMovementCount ?: 0.0,
                )
            } ?: stringResource(R.string.trends_fetal_movement_body),
            points = summary.fetalMovementPoints,
            emptyActionResId = R.string.trends_record_fetal_movement,
            recordType = CalendarRecordType.FetalMovement,
            onRecordClick = onRecordClick,
        )
        TrendCard(
            titleResId = R.string.trends_exercise_title,
            body = summary.latestExerciseMinutes?.let {
                stringResource(
                    R.string.trends_exercise_summary,
                    summary.exerciseRecordCount,
                    it,
                    summary.totalExerciseMinutes,
                )
            } ?: stringResource(R.string.trends_exercise_body),
            points = summary.exercisePoints,
            emptyActionResId = R.string.trends_record_exercise,
            recordType = CalendarRecordType.Exercise,
            onRecordClick = onRecordClick,
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun TrendRangeSelector(
    selectedRange: TrendRange,
    onRangeSelected: (TrendRange) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TrendRange.entries.forEach { range ->
            FilterChip(
                selected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                label = { Text(range.toDisplayText()) },
            )
        }
    }
}

@Composable
private fun TrendRange.toDisplayText(): String = when (this) {
    TrendRange.Last7Days -> stringResource(R.string.trends_range_7_days)
    TrendRange.Last30Days -> stringResource(R.string.trends_range_30_days)
    TrendRange.All -> stringResource(R.string.trends_range_all)
}

@Composable
private fun TrendCard(
    @StringRes titleResId: Int,
    body: String,
    points: List<TrendPoint>,
    @StringRes emptyActionResId: Int,
    recordType: CalendarRecordType,
    onRecordClick: (CalendarRecordType) -> Unit,
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
            if (points.isNotEmpty()) {
                TrendLineChart(points = points)
            } else {
                Button(onClick = { onRecordClick(recordType) }) {
                    Text(text = stringResource(emptyActionResId))
                }
            }
        }
    }
}

@Composable
private fun TrendLineChart(points: List<TrendPoint>) {
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 8.dp),
    ) {
        val horizontalPadding = 8.dp.toPx()
        val verticalPadding = 12.dp.toPx()
        val chartWidth = max(1f, size.width - horizontalPadding * 2)
        val chartHeight = max(1f, size.height - verticalPadding * 2)
        val values = points.map(TrendPoint::value)
        val minValue = values.minOrNull() ?: 0.0
        val maxValue = values.maxOrNull() ?: minValue
        val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0
        val offsets = points.mapIndexed { index, point ->
            val x = if (points.size == 1) {
                size.width / 2f
            } else {
                horizontalPadding + chartWidth * index / (points.lastIndex)
            }
            val y = verticalPadding + chartHeight * (1f - ((point.value - minValue) / range).toFloat())
            Offset(x, y)
        }

        drawLine(
            color = gridColor,
            start = Offset(horizontalPadding, verticalPadding),
            end = Offset(size.width - horizontalPadding, verticalPadding),
            strokeWidth = 1.dp.toPx(),
        )
        drawLine(
            color = gridColor,
            start = Offset(horizontalPadding, size.height - verticalPadding),
            end = Offset(size.width - horizontalPadding, size.height - verticalPadding),
            strokeWidth = 1.dp.toPx(),
        )
        offsets.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
            )
        }
        offsets.forEach { offset ->
            drawCircle(
                color = pointColor,
                radius = 4.dp.toPx(),
                center = offset,
            )
        }
    }
}

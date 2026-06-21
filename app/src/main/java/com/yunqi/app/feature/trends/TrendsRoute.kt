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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunqi.app.R

@Composable
fun TrendsRoute(contentPadding: PaddingValues) {
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
            bodyResId = R.string.trends_weight_body,
        )
        TrendCard(
            titleResId = R.string.trends_fetal_movement_title,
            bodyResId = R.string.trends_fetal_movement_body,
        )
        TrendCard(
            titleResId = R.string.trends_exercise_title,
            bodyResId = R.string.trends_exercise_body,
        )
    }
}

@Composable
private fun TrendCard(
    @StringRes titleResId: Int,
    @StringRes bodyResId: Int,
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
                text = stringResource(bodyResId),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


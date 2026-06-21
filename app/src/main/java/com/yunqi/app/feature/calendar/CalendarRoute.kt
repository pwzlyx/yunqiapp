package com.yunqi.app.feature.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunqi.app.R

@Composable
fun CalendarRoute(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.calendar_month_placeholder))
            }
        }

        Text(
            text = stringResource(R.string.calendar_today_records),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.calendar_records_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = {}) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Text(stringResource(R.string.calendar_add_record))
        }
    }
}


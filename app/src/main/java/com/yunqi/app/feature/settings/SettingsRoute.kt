package com.yunqi.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunqi.app.R

@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    onPregnancyProfileClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.settings_pregnancy_profile),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(stringResource(R.string.settings_pregnancy_profile_body))
        Button(
            onClick = onPregnancyProfileClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.settings_edit_pregnancy_profile))
        }
        HorizontalDivider()
        Text(
            text = stringResource(R.string.settings_reminders),
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = true, onCheckedChange = {})
        Text(
            text = stringResource(R.string.medical_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


package com.yunqi.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsRoute(contentPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "我的",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "孕期档案",
            style = MaterialTheme.typography.titleMedium,
        )
        Text("设置末次月经、预产期或当前孕周。")
        HorizontalDivider()
        Text(
            text = "提醒设置",
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(checked = true, onCheckedChange = {})
        Text(
            text = "医学建议仅供记录和健康管理参考，不能替代医生建议。",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


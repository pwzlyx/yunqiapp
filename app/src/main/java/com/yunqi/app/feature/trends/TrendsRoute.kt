package com.yunqi.app.feature.trends

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
import androidx.compose.ui.unit.dp

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
            text = "趋势",
            style = MaterialTheme.typography.headlineMedium,
        )
        TrendCard(title = "体重趋势", body = "记录两次以上体重后展示变化曲线。")
        TrendCard(title = "胎动趋势", body = "记录胎动后帮助回看日常模式。")
        TrendCard(title = "运动统计", body = "记录运动时长后展示每周活动量。")
    }
}

@Composable
private fun TrendCard(
    title: String,
    body: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = body, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}


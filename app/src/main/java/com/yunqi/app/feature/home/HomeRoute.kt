package com.yunqi.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yunqi.app.domain.pregnancy.PregnancyCalculator
import java.time.LocalDate

@Composable
fun HomeRoute(contentPadding: PaddingValues) {
    val progress = PregnancyCalculator.fromLastMenstrualPeriod(
        lmpDate = LocalDate.now().minusWeeks(18).minusDays(3),
    )
    val reminders = listOf("记录今日体重", "留意胎动规律", "晚饭后散步 20 分钟")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "今天",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${progress.week} 周 ${progress.day} 天",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "距离预产期还有 ${progress.daysUntilDueDate} 天",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        item {
            SectionCard(title = "今日提醒") {
                reminders.forEach { reminder ->
                    AssistChip(onClick = {}, label = { Text(reminder) })
                }
            }
        }

        item {
            SectionCard(title = "饮食重点") {
                Text("保持规律进餐，优先选择优质蛋白、蔬菜、水果和全谷物。孕期饮食建议仅供参考，特殊情况请遵医嘱。")
            }
        }

        item {
            SectionCard(title = "运动建议") {
                Text("若医生没有限制运动，可选择散步、孕期瑜伽等温和活动。运动中如有不适，应停止并咨询医生。")
            }
        }

        item {
            Text(
                text = "本 App 用于记录与健康管理提醒，不能替代医生诊断或治疗建议。",
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

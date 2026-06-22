package com.yunqi.app.data.content

import androidx.annotation.StringRes

enum class PregnancyContentCategory {
    Diet,
    Exercise,
    AntenatalCare,
    Safety,
}

enum class PregnancyContentRiskLevel {
    Normal,
    Caution,
    Urgent,
}

data class PregnancyContentCard(
    val id: String,
    val category: PregnancyContentCategory,
    val weekStart: Int,
    val weekEnd: Int,
    @StringRes val titleResId: Int,
    @StringRes val bodyResId: Int,
    @StringRes val sourceNameResId: Int,
    val sourceUrl: String,
    val reviewedAt: String,
    val riskLevel: PregnancyContentRiskLevel,
    val locale: String = "zh-CN",
)

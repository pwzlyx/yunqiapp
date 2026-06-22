package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.Trimester
import com.yunqi.app.domain.pregnancy.calculateProgress
import java.time.LocalDate

internal enum class PregnancySetupPreviewWarning {
    ConfirmPastDueDate,
}

/**
 * Derives non-diagnostic setup warnings from a valid pregnancy profile preview.
 */
internal fun PregnancyProfileParseResult.previewWarningFor(today: LocalDate): PregnancySetupPreviewWarning? {
    val profile = (this as? PregnancyProfileParseResult.Success)?.profile ?: return null
    val progress = profile.calculateProgress(today = today)
    return when (progress.trimester) {
        Trimester.PostDue -> PregnancySetupPreviewWarning.ConfirmPastDueDate
        Trimester.First,
        Trimester.Second,
        Trimester.Third -> null
    }
}

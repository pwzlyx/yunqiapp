package com.yunqi.app.feature.setup

import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PregnancySetupPreviewPolicyTest {
    @Test
    fun `warns when preview is past forty two weeks`() {
        val result = PregnancyProfileParseResult.Success(
            PregnancyProfile(
                calculationMethod = PregnancyCalculationMethod.DueDate,
                lmpDate = null,
                dueDate = LocalDate.of(2026, 6, 1),
                conceptionDate = null,
                gestationalWeekAtSetup = null,
                gestationalDayAtSetup = null,
                setupDate = LocalDate.of(2026, 6, 22),
            ),
        )

        val warning = result.previewWarningFor(today = LocalDate.of(2026, 6, 22))

        assertEquals(PregnancySetupPreviewWarning.ConfirmPastDueDate, warning)
    }

    @Test
    fun `does not warn within the standard pregnancy range`() {
        val result = PregnancyProfileParseResult.Success(
            PregnancyProfile(
                calculationMethod = PregnancyCalculationMethod.DueDate,
                lmpDate = null,
                dueDate = LocalDate.of(2026, 7, 20),
                conceptionDate = null,
                gestationalWeekAtSetup = null,
                gestationalDayAtSetup = null,
                setupDate = LocalDate.of(2026, 6, 22),
            ),
        )

        val warning = result.previewWarningFor(today = LocalDate.of(2026, 6, 22))

        assertNull(warning)
    }

    @Test
    fun `does not warn for invalid input preview`() {
        val warning = PregnancyProfileParseResult.InvalidDate.previewWarningFor(
            today = LocalDate.of(2026, 6, 22),
        )

        assertNull(warning)
    }
}

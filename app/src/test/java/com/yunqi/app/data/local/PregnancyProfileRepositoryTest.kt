package com.yunqi.app.data.local

import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PregnancyProfileRepositoryTest {
    @Test
    fun `parses stored local dates safely`() {
        assertEquals(
            LocalDate.of(2026, 6, 21),
            "2026-06-21".parseStoredLocalDateOrNull(),
        )
        assertEquals(
            LocalDate.of(2026, 6, 21),
            " 2026-06-21 ".parseStoredLocalDateOrNull(),
        )
    }

    @Test
    fun `returns null for blank or corrupt stored dates`() {
        assertNull(null.parseStoredLocalDateOrNull())
        assertNull("   ".parseStoredLocalDateOrNull())
        assertNull("2026/06/21".parseStoredLocalDateOrNull())
    }

    @Test
    fun `accepts stored profile when required calculation fields exist`() {
        assertTrue(
            profile(
                method = PregnancyCalculationMethod.LastMenstrualPeriod,
                lmpDate = LocalDate.of(2026, 1, 1),
            ).hasUsableCalculationFields(),
        )
        assertTrue(
            profile(
                method = PregnancyCalculationMethod.DueDate,
                dueDate = LocalDate.of(2026, 10, 8),
            ).hasUsableCalculationFields(),
        )
        assertTrue(
            profile(
                method = PregnancyCalculationMethod.ConceptionDate,
                conceptionDate = LocalDate.of(2026, 1, 15),
            ).hasUsableCalculationFields(),
        )
        assertTrue(
            profile(
                method = PregnancyCalculationMethod.CurrentGestationalAge,
                gestationalWeekAtSetup = 10,
                gestationalDayAtSetup = 2,
            ).hasUsableCalculationFields(),
        )
    }

    @Test
    fun `rejects stored profile when required calculation fields are missing`() {
        assertFalse(
            profile(method = PregnancyCalculationMethod.LastMenstrualPeriod)
                .hasUsableCalculationFields(),
        )
        assertFalse(
            profile(method = PregnancyCalculationMethod.DueDate)
                .hasUsableCalculationFields(),
        )
        assertFalse(
            profile(method = PregnancyCalculationMethod.ConceptionDate)
                .hasUsableCalculationFields(),
        )
        assertFalse(
            profile(
                method = PregnancyCalculationMethod.CurrentGestationalAge,
                gestationalWeekAtSetup = 10,
                gestationalDayAtSetup = null,
            ).hasUsableCalculationFields(),
        )
    }

    @Test
    fun `rejects stored current gestational age beyond supported range`() {
        assertFalse(
            profile(
                method = PregnancyCalculationMethod.CurrentGestationalAge,
                gestationalWeekAtSetup = 43,
                gestationalDayAtSetup = 0,
            ).hasUsableCalculationFields(),
        )
        assertFalse(
            profile(
                method = PregnancyCalculationMethod.CurrentGestationalAge,
                gestationalWeekAtSetup = 12,
                gestationalDayAtSetup = 7,
            ).hasUsableCalculationFields(),
        )
        assertFalse(
            profile(
                method = PregnancyCalculationMethod.CurrentGestationalAge,
                gestationalWeekAtSetup = 42,
                gestationalDayAtSetup = 1,
            ).hasUsableCalculationFields(),
        )
    }

    private fun profile(
        method: PregnancyCalculationMethod,
        lmpDate: LocalDate? = null,
        dueDate: LocalDate? = null,
        conceptionDate: LocalDate? = null,
        gestationalWeekAtSetup: Int? = null,
        gestationalDayAtSetup: Int? = null,
    ): PregnancyProfile = PregnancyProfile(
        calculationMethod = method,
        lmpDate = lmpDate,
        dueDate = dueDate,
        conceptionDate = conceptionDate,
        gestationalWeekAtSetup = gestationalWeekAtSetup,
        gestationalDayAtSetup = gestationalDayAtSetup,
        setupDate = LocalDate.of(2026, 6, 21),
    )
}

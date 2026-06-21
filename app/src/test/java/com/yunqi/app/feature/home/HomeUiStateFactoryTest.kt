package com.yunqi.app.feature.home

import com.yunqi.app.data.content.PregnancyContentCategory
import com.yunqi.app.domain.pregnancy.PregnancyCalculationMethod
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUiStateFactoryTest {
    @Test
    fun `returns missing state before profile setup`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )

        assertEquals(HomeUiState.ProfileMissing, factory.create(null))
    }

    @Test
    fun `builds ready state with pregnancy progress and content cards`() {
        val factory = HomeUiStateFactory(
            todayProvider = { LocalDate.of(2026, 6, 21) },
        )
        val profile = PregnancyProfile(
            calculationMethod = PregnancyCalculationMethod.LastMenstrualPeriod,
            lmpDate = LocalDate.of(2026, 3, 1),
            dueDate = null,
            gestationalWeekAtSetup = null,
            gestationalDayAtSetup = null,
            setupDate = LocalDate.of(2026, 6, 1),
        )

        val state = factory.create(profile) as HomeUiState.Ready

        assertEquals(16, state.progress.week)
        assertEquals(0, state.progress.day)
        assertEquals(PregnancyCalculationMethod.LastMenstrualPeriod, state.calculationMethod)
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Diet })
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Exercise })
        assertTrue(state.contentCards.any { it.category == PregnancyContentCategory.Safety })
    }
}

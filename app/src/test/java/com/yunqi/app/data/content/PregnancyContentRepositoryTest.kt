package com.yunqi.app.data.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PregnancyContentRepositoryTest {
    private val repository = PregnancyContentRepository()

    @Test
    fun `returns four categories for first trimester`() {
        val cards = repository.cardsForWeek(8)

        assertEquals(4, cards.size)
        assertTrue(cards.any { it.category == PregnancyContentCategory.Diet })
        assertTrue(cards.any { it.category == PregnancyContentCategory.Exercise })
        assertTrue(cards.any { it.category == PregnancyContentCategory.AntenatalCare })
        assertTrue(cards.any { it.category == PregnancyContentCategory.Safety })
    }

    @Test
    fun `returns four categories for second trimester`() {
        val cards = repository.cardsForWeek(20)

        assertEquals(4, cards.size)
        assertTrue(cards.all { it.weekStart == 14 })
    }

    @Test
    fun `returns third trimester content for post due week`() {
        val cards = repository.cardsForWeek(43)

        assertEquals(4, cards.size)
        assertTrue(cards.all { it.weekEnd == 42 })
    }
}

package com.yunqi.app.data.content

import java.time.LocalDate
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

    @Test
    fun `content cards expose traceable medical metadata`() {
        val cards = representativeCards()

        assertTrue(cards.all { it.sourceUrl.startsWith("https://") })
        assertTrue(cards.all { runCatching { LocalDate.parse(it.reviewedAt) }.isSuccess })
        assertTrue(cards.all { it.locale == "zh-CN" })
        assertEquals(cards.size, cards.map(PregnancyContentCard::id).toSet().size)
        assertEquals(PregnancyContentRiskLevel.entries.toSet(), cards.map(PregnancyContentCard::riskLevel).toSet())
    }

    @Test
    fun `safety cards carry caution or urgent risk levels`() {
        val safetyCards = representativeCards()
            .filter { it.category == PregnancyContentCategory.Safety }

        assertTrue(safetyCards.isNotEmpty())
        assertTrue(safetyCards.none { it.riskLevel == PregnancyContentRiskLevel.Normal })
    }

    private fun representativeCards(): List<PregnancyContentCard> =
        listOf(8L, 20L, 34L).flatMap(repository::cardsForWeek)
}

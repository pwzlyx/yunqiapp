package com.yunqi.app.feature.home

import com.yunqi.app.data.content.PregnancyContentCategory
import com.yunqi.app.data.content.PregnancyContentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeContentVisibilityTest {
    private val contentCards = PregnancyContentRepository().cardsForWeek(20)

    @Test
    fun `hides exercise guidance using real content ids`() {
        val exerciseCard = contentCards.single { it.category == PregnancyContentCategory.Exercise }

        val visibleCards = HomeContentVisibility.visibleExerciseCards(
            contentCards = contentCards,
            hiddenContentIds = setOf(exerciseCard.id),
        )

        assertTrue(visibleCards.isEmpty())
    }

    @Test
    fun `returns hidden exercise guidance for restore actions`() {
        val exerciseCard = contentCards.single { it.category == PregnancyContentCategory.Exercise }

        val hiddenCards = HomeContentVisibility.hiddenExerciseCards(
            contentCards = contentCards,
            hiddenContentIds = setOf(exerciseCard.id),
        )

        assertEquals(listOf(exerciseCard), hiddenCards)
    }

    @Test
    fun `ignores hidden ids from non exercise content`() {
        val dietCard = contentCards.single { it.category == PregnancyContentCategory.Diet }

        val visibleCards = HomeContentVisibility.visibleExerciseCards(
            contentCards = contentCards,
            hiddenContentIds = setOf(dietCard.id),
        )

        assertTrue(visibleCards.all { it.category == PregnancyContentCategory.Exercise })
        assertFalse(visibleCards.any { it.id == dietCard.id })
        assertEquals(1, visibleCards.size)
    }
}

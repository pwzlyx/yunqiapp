package com.yunqi.app.feature.home

import com.yunqi.app.data.content.PregnancyContentCard
import com.yunqi.app.data.content.PregnancyContentCategory

/**
 * Keeps user-hidden exercise guidance out of the default Home plan while preserving a restore path.
 */
internal object HomeContentVisibility {
    fun visibleExerciseCards(
        contentCards: List<PregnancyContentCard>,
        hiddenContentIds: Set<String>,
    ): List<PregnancyContentCard> = exerciseCards(contentCards)
        .filterNot { it.id in hiddenContentIds }

    fun hiddenExerciseCards(
        contentCards: List<PregnancyContentCard>,
        hiddenContentIds: Set<String>,
    ): List<PregnancyContentCard> = exerciseCards(contentCards)
        .filter { it.id in hiddenContentIds }

    private fun exerciseCards(contentCards: List<PregnancyContentCard>): List<PregnancyContentCard> =
        contentCards.filter { it.category == PregnancyContentCategory.Exercise }
}

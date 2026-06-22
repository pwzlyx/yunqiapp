package com.yunqi.app.feature.home

import com.yunqi.app.data.content.PregnancyContentRepository
import com.yunqi.app.data.local.ContentStatus
import com.yunqi.app.domain.pregnancy.PregnancyProfile
import com.yunqi.app.domain.pregnancy.calculateProgress
import java.time.LocalDate

class HomeUiStateFactory(
    private val contentRepository: PregnancyContentRepository = PregnancyContentRepository(),
    private val todayProvider: () -> LocalDate = LocalDate::now,
) {
    /**
     * Builds render-ready home state from the local pregnancy profile.
     */
    fun create(
        profile: PregnancyProfile?,
        contentStatus: ContentStatus = ContentStatus(),
    ): HomeUiState {
        if (profile == null) return HomeUiState.ProfileMissing

        val progress = profile.calculateProgress(today = todayProvider())
        return HomeUiState.Ready(
            progress = progress,
            calculationMethod = profile.calculationMethod,
            exerciseRestricted = profile.exerciseRestricted,
            contentCards = contentRepository.cardsForWeek(progress.week),
            contentStatus = contentStatus,
        )
    }
}

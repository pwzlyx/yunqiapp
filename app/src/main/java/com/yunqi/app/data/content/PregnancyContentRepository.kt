package com.yunqi.app.data.content

import com.yunqi.app.R

class PregnancyContentRepository {
    /**
     * Returns locally reviewed content cards that match the current gestational week.
     */
    fun cardsForWeek(week: Long): List<PregnancyContentCard> {
        val normalizedWeek = week.coerceIn(0, 42).toInt()
        return contentCards.filter { normalizedWeek in it.weekStart..it.weekEnd }
    }

    private val contentCards = listOf(
        PregnancyContentCard(
            id = "diet_first_trimester_folic_acid",
            category = PregnancyContentCategory.Diet,
            weekStart = 0,
            weekEnd = 13,
            titleResId = R.string.content_diet_first_title,
            bodyResId = R.string.content_diet_first_body,
            sourceNameResId = R.string.source_cdc_folic_acid,
            sourceUrl = "https://www.cdc.gov/folic-acid/about/index.html",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "exercise_first_trimester_moderate",
            category = PregnancyContentCategory.Exercise,
            weekStart = 0,
            weekEnd = 13,
            titleResId = R.string.content_exercise_first_title,
            bodyResId = R.string.content_exercise_first_body,
            sourceNameResId = R.string.source_cdc_physical_activity,
            sourceUrl = "https://www.cdc.gov/physical-activity-basics/guidelines/healthy-pregnant-or-postpartum-women.html",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "safety_first_trimester_warning_signs",
            category = PregnancyContentCategory.Safety,
            weekStart = 0,
            weekEnd = 13,
            titleResId = R.string.content_safety_first_title,
            bodyResId = R.string.content_safety_first_body,
            sourceNameResId = R.string.source_who_antenatal_care,
            sourceUrl = "https://www.who.int/publications/i/item/9789241549912",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "diet_second_trimester_fish",
            category = PregnancyContentCategory.Diet,
            weekStart = 14,
            weekEnd = 27,
            titleResId = R.string.content_diet_second_title,
            bodyResId = R.string.content_diet_second_body,
            sourceNameResId = R.string.source_fda_fish,
            sourceUrl = "https://www.fda.gov/food/consumers/advice-about-eating-fish",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "exercise_second_trimester_walk",
            category = PregnancyContentCategory.Exercise,
            weekStart = 14,
            weekEnd = 27,
            titleResId = R.string.content_exercise_second_title,
            bodyResId = R.string.content_exercise_second_body,
            sourceNameResId = R.string.source_cdc_physical_activity,
            sourceUrl = "https://www.cdc.gov/physical-activity-basics/guidelines/healthy-pregnant-or-postpartum-women.html",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "safety_second_trimester_movement_pattern",
            category = PregnancyContentCategory.Safety,
            weekStart = 14,
            weekEnd = 27,
            titleResId = R.string.content_safety_second_title,
            bodyResId = R.string.content_safety_second_body,
            sourceNameResId = R.string.source_nhs_baby_movements,
            sourceUrl = "https://www.nhs.uk/pregnancy/keeping-well/your-babys-movements/",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "diet_third_trimester_regular_meals",
            category = PregnancyContentCategory.Diet,
            weekStart = 28,
            weekEnd = 42,
            titleResId = R.string.content_diet_third_title,
            bodyResId = R.string.content_diet_third_body,
            sourceNameResId = R.string.source_who_antenatal_care,
            sourceUrl = "https://www.who.int/publications/i/item/9789241549912",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "exercise_third_trimester_comfort",
            category = PregnancyContentCategory.Exercise,
            weekStart = 28,
            weekEnd = 42,
            titleResId = R.string.content_exercise_third_title,
            bodyResId = R.string.content_exercise_third_body,
            sourceNameResId = R.string.source_cdc_physical_activity,
            sourceUrl = "https://www.cdc.gov/physical-activity-basics/guidelines/healthy-pregnant-or-postpartum-women.html",
            reviewedAt = "2026-06-21",
        ),
        PregnancyContentCard(
            id = "safety_third_trimester_reduced_movement",
            category = PregnancyContentCategory.Safety,
            weekStart = 28,
            weekEnd = 42,
            titleResId = R.string.content_safety_third_title,
            bodyResId = R.string.content_safety_third_body,
            sourceNameResId = R.string.source_nhs_baby_movements,
            sourceUrl = "https://www.nhs.uk/pregnancy/keeping-well/your-babys-movements/",
            reviewedAt = "2026-06-21",
        ),
    )
}


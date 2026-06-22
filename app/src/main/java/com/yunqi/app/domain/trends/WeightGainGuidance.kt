package com.yunqi.app.domain.trends

import com.yunqi.app.domain.pregnancy.PregnancyBabyCount
import com.yunqi.app.domain.pregnancy.PregnancyProfile

data class WeightGainGuidance(
    val bmi: Double,
    val category: PrePregnancyBmiCategory,
    val minGainKg: Double,
    val maxGainKg: Double,
)

enum class PrePregnancyBmiCategory {
    Underweight,
    Normal,
    Overweight,
    Obese,
}

object WeightGainGuidanceCalculator {
    /**
     * Estimates CDC-style total pregnancy weight gain guidance from local profile data.
     */
    fun calculate(profile: PregnancyProfile?): WeightGainGuidance? {
        val heightCm = profile?.heightCm ?: return null
        val prePregnancyWeightKg = profile.prePregnancyWeightKg ?: return null
        if (heightCm <= 0.0 || prePregnancyWeightKg <= 0.0) return null

        val heightM = heightCm / 100.0
        val bmi = prePregnancyWeightKg / (heightM * heightM)
        val category = bmi.toPrePregnancyBmiCategory()
        val range = category.recommendedGainRange(profile.babyCount)

        return WeightGainGuidance(
            bmi = bmi,
            category = category,
            minGainKg = range.first,
            maxGainKg = range.second,
        )
    }
}

private fun Double.toPrePregnancyBmiCategory(): PrePregnancyBmiCategory = when {
    this < 18.5 -> PrePregnancyBmiCategory.Underweight
    this < 25.0 -> PrePregnancyBmiCategory.Normal
    this < 30.0 -> PrePregnancyBmiCategory.Overweight
    else -> PrePregnancyBmiCategory.Obese
}

private fun PrePregnancyBmiCategory.recommendedGainRange(
    babyCount: PregnancyBabyCount,
): Pair<Double, Double> = when (babyCount) {
    PregnancyBabyCount.Singleton -> when (this) {
        PrePregnancyBmiCategory.Underweight -> 12.7 to 18.1
        PrePregnancyBmiCategory.Normal -> 11.3 to 15.9
        PrePregnancyBmiCategory.Overweight -> 6.8 to 11.3
        PrePregnancyBmiCategory.Obese -> 5.0 to 9.1
    }

    PregnancyBabyCount.Twin -> when (this) {
        PrePregnancyBmiCategory.Underweight -> 22.7 to 28.1
        PrePregnancyBmiCategory.Normal -> 16.8 to 24.5
        PrePregnancyBmiCategory.Overweight -> 14.1 to 22.7
        PrePregnancyBmiCategory.Obese -> 11.3 to 19.1
    }
}

package com.example.util

import com.example.model.CandidateProfile
import com.example.model.CompatibilityBreakdown
import com.example.model.CurrentUserProfile
import com.example.model.MatchingWeights
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object MatchingEngine {

    /**
     * Calculates a comprehensive, deterministic compatibility score between the current user
     * and a prospective marriage candidate based on religious alignment, life goals,
     * family values, geographic proximity, and shared interests.
     */
    fun calculateCompatibility(
        user: CurrentUserProfile,
        candidate: CandidateProfile,
        weights: MatchingWeights = MatchingWeights()
    ): CompatibilityBreakdown {
        // 1. Religious Alignment (0 - 100)
        var relScore = 70
        if (user.prayersHabit.contains("الخمس") && candidate.prayersHabit.contains("الخمس")) {
            relScore += 15
        }
        if (user.religiousPractice == candidate.religiousPractice || candidate.religiousPractice.contains("ملتزم")) {
            relScore += 10
        }
        if (candidate.halalFood.contains("حلال")) {
            relScore += 5
        }
        relScore = min(100, relScore)

        // 2. Marriage Intentions & Family Values (0 - 100)
        var intentScore = 75
        if (candidate.marriageGoal.isNotBlank()) {
            intentScore += 10
        }
        if (candidate.desireForChildren.contains("نعم") && user.desireForChildren.contains("نعم")) {
            intentScore += 10
        }
        if (candidate.willingToRelocate) {
            intentScore += 5
        }
        intentScore = min(100, intentScore)

        // 3. Shared Interests & Lifestyle (0 - 100)
        val userTokens = (user.interests + " " + user.hobbies + " " + user.lifestyle)
            .split("،", ",", " ", "•")
            .map { it.trim().lowercase() }
            .filter { it.length > 2 }
            .toSet()
        val candTokens = (candidate.interests + " " + candidate.hobbies + " " + candidate.lifestyle)
            .split("،", ",", " ", "•")
            .map { it.trim().lowercase() }
            .filter { it.length > 2 }
            .toSet()

        val commonCount = userTokens.intersect(candTokens).size
        val interestScore = min(100, max(65, 70 + commonCount * 8))

        // 4. Distance & Proximity (0 - 100)
        val distance = candidate.distanceKm
        val distScore = when {
            distance <= 15 -> 100
            distance <= 35 -> 92
            distance <= 75 -> 84
            distance <= 150 -> 76
            else -> 65
        }

        // 5. Preferences & Demographics (0 - 100)
        val ageDiff = abs(user.age - candidate.age)
        val ageScore = when {
            ageDiff <= 3 -> 98
            ageDiff <= 6 -> 90
            ageDiff <= 10 -> 80
            else -> 70
        }
        val prefScore = (ageScore + (if (candidate.isVerified) 100 else 80)) / 2

        // Weighted Overall Compatibility Score
        val weightedSum = (
            relScore * weights.religiousWeight +
            intentScore * weights.intentionsWeight +
            interestScore * weights.interestsWeight +
            distScore * weights.distanceWeight +
            prefScore * weights.preferencesWeight +
            (if (candidate.isVerified) 100 else 80) * weights.completenessWeight
        )
        val totalScore = min(99, max(68, weightedSum.toInt()))

        val summaryAr = when {
            totalScore >= 90 -> "توافق شرعي وأخلاقي استثنائي مع تقارب كبير في أهداف الزواج ونمط الحياة والمسافة الجغرافية."
            totalScore >= 80 -> "توافق طيب جداً في الالتزام الديني والاهتمامات الشخصية مع بيئة مناسبة لبناء بيت مسلم سعيد."
            else -> "توافق مقبول مع نقاط التقاء طيبة في الرغبة بتكوين أسرة واستقرار الحياة الزوجية."
        }

        val summaryEn = when {
            totalScore >= 90 -> "Exceptional religious and ethical compatibility with strong alignment on marriage goals."
            totalScore >= 80 -> "Very good harmony in spiritual values and personal interests for a halal marriage."
            else -> "Good compatibility with aligned family intentions and mutual respect."
        }

        return CompatibilityBreakdown(
            totalScore = totalScore,
            religiousScore = relScore,
            intentionsScore = intentScore,
            interestsScore = interestScore,
            distanceScore = distScore,
            preferencesScore = prefScore,
            summaryAr = summaryAr,
            summaryEn = summaryEn
        )
    }
}

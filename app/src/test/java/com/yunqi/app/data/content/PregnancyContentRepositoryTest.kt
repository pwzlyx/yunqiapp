package com.yunqi.app.data.content

import java.nio.file.Paths
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

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
    fun `every supported pregnancy week exposes all MVP content categories`() {
        val expectedCategories = PregnancyContentCategory.entries.toSet()

        (0L..42L).forEach { week ->
            val cards = repository.cardsForWeek(week)

            assertEquals("week $week", expectedCategories, cards.map(PregnancyContentCard::category).toSet())
            assertEquals("week $week", expectedCategories.size, cards.size)
        }
    }

    @Test
    fun `content cards expose traceable medical metadata`() {
        val cards = representativeCards()

        assertTrue(cards.all { it.sourceUrl.startsWith("https://") })
        assertTrue(cards.all { it.sourceUrl.isNotBlank() })
        assertTrue(cards.all { runCatching { LocalDate.parse(it.reviewedAt) }.isSuccess })
        assertTrue(cards.all { it.reviewedAt.isNotBlank() })
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

    @Test
    fun `diet guidance body copy avoids medical promise terms`() {
        val forbiddenTerms = listOf("保证", "治愈", "诊断")
        val dietBodyStrings = contentStringValues()
            .filterKeys { name -> name.startsWith("content_diet_") && name.endsWith("_body") }

        assertTrue(dietBodyStrings.isNotEmpty())
        dietBodyStrings.forEach { (name, value) ->
            forbiddenTerms.forEach { term ->
                assertTrue("$name should not contain $term", term !in value)
            }
        }
    }

    @Test
    fun `second and third trimester exercise guidance avoids supine recommendations`() {
        val forbiddenTerms = listOf("仰卧", "平躺", "supine")
        val exerciseStrings = contentStringValues()
            .filterKeys { name ->
                name.startsWith("content_exercise_second_") ||
                    name.startsWith("content_exercise_third_")
            }

        assertTrue(exerciseStrings.isNotEmpty())
        exerciseStrings.forEach { (name, value) ->
            forbiddenTerms.forEach { term ->
                assertTrue("$name should not contain $term", !value.contains(term, ignoreCase = true))
            }
        }
    }

    private fun representativeCards(): List<PregnancyContentCard> =
        listOf(8L, 20L, 34L).flatMap(repository::cardsForWeek)

    private fun contentStringValues(): Map<String, String> {
        val stringsFile = Paths.get("src", "main", "res", "values", "strings.xml").toFile()
        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(stringsFile)
        val nodes = document.getElementsByTagName("string")

        return (0 until nodes.length)
            .map { nodes.item(it) as Element }
            .associate { element ->
                element.getAttribute("name") to element.textContent
            }
    }
}

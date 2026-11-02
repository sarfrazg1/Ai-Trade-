package com.example

import com.example.data.repository.FinancialLearningCenterData
import com.example.data.repository.NewsRepository
import com.example.domain.model.ConceptCategory
import com.example.domain.model.MarketImpactDirection
import com.example.domain.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NewsAndLearningCenterUnitTest {

    @Test
    fun testFinancialLearningCenterCurriculumCompleteness() {
        // Must contain exactly 22 economic + 28 crypto concepts = 50 total
        assertEquals(22, FinancialLearningCenterData.economicConcepts.size)
        assertEquals(28, FinancialLearningCenterData.cryptoConcepts.size)
        assertEquals(50, FinancialLearningCenterData.allConcepts.size)

        // Verify each concept has rich, beginner-friendly Roman Urdu and everyday examples
        FinancialLearningCenterData.allConcepts.forEach { concept ->
            assertTrue("Term must not be blank", concept.term.isNotBlank())
            assertTrue("Full form must not be blank", concept.fullForm.isNotBlank())
            assertTrue("Simple Urdu meaning must not be blank", concept.simpleUrduMeaning.isNotBlank())
            assertTrue("Everyday example must not be blank", concept.everydayExample.isNotBlank())
            assertTrue("Why it matters must not be blank", concept.whyItMatters.isNotBlank())
            assertTrue("Market impact explanation must not be blank", concept.marketImpactExplanation.isNotBlank())

            // MANDATORY RULE: Strictly NO quizzes, MCQs, questions or test answers in learning center
            assertFalse("Must not contain quiz references", concept.simpleUrduMeaning.contains("quiz", ignoreCase = true))
            assertFalse("Must not contain MCQ references", concept.simpleUrduMeaning.contains("mcq", ignoreCase = true))
        }
    }

    @Test
    fun testNewsCategories() {
        val categories = NewsCategory.values()
        assertTrue("Categories must include core economic and crypto topics", categories.size >= 17)
        assertTrue(categories.contains(NewsCategory.ALL))
        assertTrue(categories.contains(NewsCategory.GLOBAL_ECONOMY))
        assertTrue(categories.contains(NewsCategory.FED_FOMC))
        assertTrue(categories.contains(NewsCategory.INFLATION_CPI))
        assertTrue(categories.contains(NewsCategory.INTEREST_RATES))
        assertTrue(categories.contains(NewsCategory.EMPLOYMENT_NFP))
        assertTrue(categories.contains(NewsCategory.GDP))
        assertTrue(categories.contains(NewsCategory.BITCOIN))
        assertTrue(categories.contains(NewsCategory.ETHEREUM))
        assertTrue(categories.contains(NewsCategory.ALTCOINS))
        assertTrue(categories.contains(NewsCategory.CRYPTO_ETFS))
        assertTrue(categories.contains(NewsCategory.REGULATION))
        assertTrue(categories.contains(NewsCategory.INSTITUTIONAL))
        assertTrue(categories.contains(NewsCategory.STABLECOINS))
        assertTrue(categories.contains(NewsCategory.GEOPOLITICAL))
        assertTrue(categories.contains(NewsCategory.GOLD_USD))
        assertTrue(categories.contains(NewsCategory.SENTIMENT))
    }

    @Test
    fun testConceptFilteringAndSearch() {
        val repo = NewsRepository()
        
        // Filter by category
        val econOnly = repo.getFinancialConcepts(category = ConceptCategory.ECONOMIC_CONCEPTS)
        assertEquals(22, econOnly.size)

        val cryptoOnly = repo.getFinancialConcepts(category = ConceptCategory.CRYPTO_CONCEPTS)
        assertEquals(28, cryptoOnly.size)

        // Search for specific concept
        val cpiSearch = repo.getFinancialConcepts(searchQuery = "CPI")
        assertTrue(cpiSearch.isNotEmpty())
        assertEquals("CPI", cpiSearch.first().term)

        val halvingSearch = repo.getFinancialConcepts(searchQuery = "Halving")
        assertTrue(halvingSearch.isNotEmpty())
        assertTrue(halvingSearch.any { it.term.contains("Halving", ignoreCase = true) })
    }

    @Test
    fun testMarketImpactDirectionLabels() {
        val directions = MarketImpactDirection.values()
        assertTrue(directions.contains(MarketImpactDirection.BULLISH))
        assertTrue(directions.contains(MarketImpactDirection.BEARISH))
        assertTrue(directions.contains(MarketImpactDirection.NEUTRAL))
        assertTrue(directions.contains(MarketImpactDirection.UNCERTAIN))
    }
}

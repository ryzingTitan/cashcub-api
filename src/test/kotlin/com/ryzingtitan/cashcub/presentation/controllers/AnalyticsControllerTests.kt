package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetSummaryService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test

class AnalyticsControllerTests {
    @Nested
    inner class GetAnalyticsData {
        @Test
        fun `returns 'OK' status with budget summaries`() =
            runTest {
                whenever(mockBudgetService.getAllForRange("10-2025", "11-2025"))
                    .thenReturn(flowOf(firstBudget, secondBudget))
                whenever(mockBudgetSummaryService.getBudgetSummary(firstBudgetId)).thenReturn(firstBudgetSummary)
                whenever(mockBudgetSummaryService.getBudgetSummary(secondBudgetId)).thenReturn(secondBudgetSummary)

                webTestClient
                    .get()
                    .uri("/api/analytics?startDate=10-2025&endDate=11-2025")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<BudgetSummary>()
                    .contains(firstBudgetSummary, secondBudgetSummary)

                verify(mockBudgetService, times(1)).getAllForRange("10-2025", "11-2025")
                verify(mockBudgetSummaryService, times(1)).getBudgetSummary(firstBudgetId)
                verify(mockBudgetSummaryService, times(1)).getBudgetSummary(secondBudgetId)
            }

        @Test
        fun `returns 'BAD_REQUEST' status when start date is missing`() =
            runTest {
                webTestClient
                    .get()
                    .uri("/api/analytics?endDate=11-2025")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest

                verify(mockBudgetService, never()).getAllForRange(any(), any())
                verify(mockBudgetSummaryService, never()).getBudgetSummary(any())
            }

        @Test
        fun `returns 'BAD_REQUEST' status when end date is missing`() =
            runTest {
                webTestClient
                    .get()
                    .uri("/api/analytics?startDate=10-2025")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isBadRequest

                verify(mockBudgetService, never()).getAllForRange(any(), any())
                verify(mockBudgetSummaryService, never()).getBudgetSummary(any())
            }
    }

    @BeforeEach
    fun setup() {
        val analyticsController = AnalyticsController(mockBudgetService, mockBudgetSummaryService)
        webTestClient = WebTestClient.bindToController(analyticsController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockBudgetService = mock<BudgetService>()
    private val mockBudgetSummaryService = mock<BudgetSummaryService>()

    private val firstBudgetId = UUID.randomUUID()
    private val secondBudgetId = UUID.randomUUID()

    private val firstBudget =
        Budget(
            id = firstBudgetId,
            month = 10,
            year = 2025,
        )

    private val secondBudget =
        Budget(
            id = secondBudgetId,
            month = 11,
            year = 2025,
        )

    private val firstBudgetSummary =
        BudgetSummary(
            id = firstBudgetId,
            month = 10,
            year = 2025,
            expectedIncome = BigDecimal("10000.25"),
            actualIncome = BigDecimal("500.00"),
            expectedExpenses = BigDecimal("800.50"),
            actualExpenses = BigDecimal("100.25"),
            budgetItems = emptyList(),
        )

    private val secondBudgetSummary =
        BudgetSummary(
            id = secondBudgetId,
            month = 11,
            year = 2025,
            expectedIncome = BigDecimal("10000.25"),
            actualIncome = BigDecimal("600.00"),
            expectedExpenses = BigDecimal("850.50"),
            actualExpenses = BigDecimal("100.55"),
            budgetItems = emptyList(),
        )
}

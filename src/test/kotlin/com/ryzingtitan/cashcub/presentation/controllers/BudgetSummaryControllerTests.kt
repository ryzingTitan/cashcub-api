package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.BudgetDoesNotExistException
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetSummaryService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test

class BudgetSummaryControllerTests {
    @Nested
    inner class GetBudgetSummary {
        @Test
        fun `returns 'OK' status with budget summary`() =
            runTest {
                whenever(mockBudgetSummaryService.getBudgetSummary(budgetId)).thenReturn(budgetSummary)

                webTestClient
                    .get()
                    .uri("/api/budgets/$budgetId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody<BudgetSummary>()
                    .isEqualTo(budgetSummary)

                verify(mockBudgetSummaryService, times(1)).getBudgetSummary(budgetId)
            }

        @Test
        fun `returns 'NOT_FOUND' status when budget does not exist`() =
            runTest {
                whenever(
                    mockBudgetSummaryService.getBudgetSummary(budgetId),
                ).thenThrow(BudgetDoesNotExistException("Budget does not exist"))

                webTestClient
                    .get()
                    .uri("/api/budgets/$budgetId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isNotFound

                verify(mockBudgetSummaryService, times(1)).getBudgetSummary(budgetId)
            }
    }

    @BeforeEach
    fun setup() {
        val budgetSummaryController = BudgetSummaryController(mockBudgetSummaryService)
        webTestClient = WebTestClient.bindToController(budgetSummaryController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockBudgetSummaryService = mock<BudgetSummaryService>()

    private val budgetId = UUID.randomUUID()
    private val budgetSummary =
        BudgetSummary(
            id = budgetId,
            month = 10,
            year = 2025,
            expectedIncome = BigDecimal("10000.25"),
            actualIncome = BigDecimal("500.00"),
            expectedExpenses = BigDecimal("800.50"),
            actualExpenses = BigDecimal("100.25"),
            budgetItems = emptyList(),
        )
}

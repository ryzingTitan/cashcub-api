package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetRequest
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.DuplicateBudgetException
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test

class BudgetControllerTests {
    @Nested
    inner class GetBudgets {
        @Test
        fun `returns 'OK' status with all budgets`() =
            runTest {
                whenever(mockBudgetService.getAll()).thenReturn(flowOf(firstBudget, secondBudget))

                webTestClient
                    .get()
                    .uri("/api/budgets")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Budget>()
                    .contains(firstBudget, secondBudget)

                verify(mockBudgetService, times(1)).getAll()
            }
    }

    @Nested
    inner class CreateBudget {
        @Test
        fun `returns 'OK' status with created budget`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.create(budgetRequest)).thenReturn(firstBudget)

                webTestClient
                    .post()
                    .uri("/api/budgets")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<Budget>()
                    .isEqualTo(firstBudget)

                verify(mockBudgetService, times(1)).create(budgetRequest)
            }

        @Test
        fun `returns 'CONFLICT' status when budget already exists`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.create(budgetRequest))
                    .thenThrow(DuplicateBudgetException("Budget already exists"))

                webTestClient
                    .post()
                    .uri("/api/budgets")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.CONFLICT)

                verify(mockBudgetService, times(1)).create(budgetRequest)
            }
    }

    @Nested
    inner class CloneBudget {
        @Test
        fun `returns 'OK' status with created budget`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.clone(budgetId, budgetRequest)).thenReturn(budgetSummary)

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/clone")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<BudgetSummary>()
                    .isEqualTo(budgetSummary)

                verify(mockBudgetService, times(1)).clone(budgetId, budgetRequest)
            }

        @Test
        fun `returns 'CONFLICT' status when budget already exists`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.clone(budgetId, budgetRequest))
                    .thenThrow(DuplicateBudgetException("Budget already exists"))

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/clone")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.CONFLICT)

                verify(mockBudgetService, times(1)).clone(budgetId, budgetRequest)
            }
    }

    @BeforeEach
    fun setup() {
        val budgetController = BudgetController(mockBudgetService)
        webTestClient = WebTestClient.bindToController(budgetController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockBudgetService = mock<BudgetService>()

    private val budgetId = UUID.randomUUID()

    private val firstBudget =
        Budget(
            id = UUID.randomUUID(),
            month = 9,
            year = 2025,
        )

    private val secondBudget =
        Budget(
            id = UUID.randomUUID(),
            month = 10,
            year = 2025,
        )

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

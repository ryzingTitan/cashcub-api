package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.UUID

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

    @BeforeEach
    fun setup() {
        val budgetController = BudgetController(mockBudgetService)
        webTestClient = WebTestClient.bindToController(budgetController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockBudgetService = mock<BudgetService>()

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
}

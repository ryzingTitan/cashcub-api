package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.DuplicateBudgetException
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.CreateBudgetRequest
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
                val createBudgetRequest = CreateBudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.create(createBudgetRequest)).thenReturn(firstBudget)

                webTestClient
                    .post()
                    .uri("/api/budgets")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(createBudgetRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<Budget>()
                    .isEqualTo(firstBudget)

                verify(mockBudgetService, times(1)).create(createBudgetRequest)
            }

        @Test
        fun `returns 'CONFLICT' status when budget already exists`() =
            runTest {
                val createBudgetRequest = CreateBudgetRequest(month = 9, year = 2025)

                whenever(mockBudgetService.create(createBudgetRequest))
                    .thenThrow(DuplicateBudgetException("Budget already exists"))

                webTestClient
                    .post()
                    .uri("/api/budgets")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(createBudgetRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.CONFLICT)

                verify(mockBudgetService, times(1)).create(createBudgetRequest)
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

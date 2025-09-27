package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.CreateBudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
import com.ryzingtitan.cashcub.domain.budgetitems.services.BudgetItemService
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

class BudgetItemControllerTests {
    @Nested
    inner class GetBudgetItemsByBudgetId {
        @Test
        fun `returns 'OK' status with all budget items for budget`() =
            runTest {
                whenever(mockBudgetItemService.getAllByBudgetId(budgetId)).thenReturn(flowOf(budgetItem))

                webTestClient
                    .get()
                    .uri("/api/budgets/$budgetId/items")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<BudgetItem>()
                    .contains(budgetItem)

                verify(mockBudgetItemService, times(1)).getAllByBudgetId(budgetId)
            }
    }

    @Nested
    inner class CreateBudgetItem {
        @Test
        fun `returns 'OK' status with created budget item`() =
            runTest {
                val createBudgetItemRequest =
                    CreateBudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.create(createBudgetItemRequest, budgetId)).thenReturn(budgetItem)

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/items")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(createBudgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<BudgetItem>()
                    .isEqualTo(budgetItem)

                verify(mockBudgetItemService, times(1)).create(createBudgetItemRequest, budgetId)
            }

        @Test
        fun `returns 'CONFLICT' status when budget item already exists`() =
            runTest {
                val createBudgetItemRequest =
                    CreateBudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.create(createBudgetItemRequest, budgetId))
                    .thenThrow(DuplicateBudgetItemException("Budget item already exists"))

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/items")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(createBudgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.CONFLICT)

                verify(mockBudgetItemService, times(1)).create(createBudgetItemRequest, budgetId)
            }
    }

    @BeforeEach
    fun setup() {
        val budgetItemController = BudgetItemController(mockBudgetItemService)
        webTestClient = WebTestClient.bindToController(budgetItemController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockBudgetItemService = mock<BudgetItemService>()

    private val budgetId = UUID.randomUUID()
    private val budgetItemId = UUID.randomUUID()
    private val name = "Budget Item"
    private val plannedAmount = BigDecimal("100.25")
    private val categoryId = UUID.randomUUID()
    private val budgetItem =
        BudgetItem(
            id = budgetItemId,
            name = name,
            plannedAmount = plannedAmount,
            budgetId = budgetId,
            categoryId = categoryId,
        )
}

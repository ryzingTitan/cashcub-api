package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.BudgetItemDoesNotExistException
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
import com.ryzingtitan.cashcub.domain.budgetitems.services.BudgetItemService
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
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test

class BudgetItemControllerTests {
    @Nested
    inner class CreateBudgetItem {
        @Test
        fun `returns 'OK' status with created budget item`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.create(budgetItemRequest, budgetId)).thenReturn(budgetItem)

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/items")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<BudgetItem>()
                    .isEqualTo(budgetItem)

                verify(mockBudgetItemService, times(1)).create(budgetItemRequest, budgetId)
            }

        @Test
        fun `returns 'CONFLICT' status when budget item already exists`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.create(budgetItemRequest, budgetId))
                    .thenThrow(DuplicateBudgetItemException("Budget item already exists"))

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/items")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isEqualTo(HttpStatus.CONFLICT)

                verify(mockBudgetItemService, times(1)).create(budgetItemRequest, budgetId)
            }
    }

    @Nested
    inner class UpdateBudgetItem {
        @Test
        fun `returns 'OK' status with updated budget item`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.update(budgetItemId, budgetId, budgetItemRequest)).thenReturn(budgetItem)

                webTestClient
                    .put()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody<BudgetItem>()
                    .isEqualTo(budgetItem)

                verify(mockBudgetItemService, times(1)).update(budgetItemId, budgetId, budgetItemRequest)
            }

        @Test
        fun `returns 'NOT FOUND' status when budget item does not exist`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemService.update(budgetItemId, budgetId, budgetItemRequest)).thenThrow(
                    BudgetItemDoesNotExistException("Budget item does not exist"),
                )

                webTestClient
                    .put()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(budgetItemRequest)
                    .exchange()
                    .expectStatus()
                    .isNotFound

                verify(mockBudgetItemService, times(1)).update(budgetItemId, budgetId, budgetItemRequest)
            }
    }

    @Nested
    inner class DeleteBudgetItem {
        @Test
        fun `returns 'OK' status`() =
            runTest {
                webTestClient
                    .delete()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk

                verify(mockBudgetItemService, times(1)).delete(budgetItemId, budgetId)
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
            actualAmount = BigDecimal("0.00"),
            budgetId = budgetId,
            categoryId = categoryId,
        )
}

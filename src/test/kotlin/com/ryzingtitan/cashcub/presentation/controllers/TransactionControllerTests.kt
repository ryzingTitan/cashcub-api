package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import com.ryzingtitan.cashcub.domain.transactions.exceptions.TransactionDoesNotExistException
import com.ryzingtitan.cashcub.domain.transactions.services.TransactionService
import com.ryzingtitan.cashcub.presentation.configuration.GlobalExceptionHandler
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.Test

class TransactionControllerTests {
    @Nested
    inner class GetTransactionsByBudgetItemIdAndBudgetId {
        @Test
        fun `returns 'OK' status with all transactions for budget item and budget`() =
            runTest {
                whenever(mockTransactionService.getAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId))
                    .thenReturn(flowOf(transaction))

                webTestClient
                    .get()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId/transactions")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Transaction>()
                    .contains(transaction)

                verify(mockTransactionService, times(1)).getAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId)
            }
    }

    @Nested
    inner class CreateTransaction {
        @Test
        fun `returns 'OK' status with created transaction`() =
            runTest {
                val transactionRequest =
                    TransactionRequest(
                        date = Instant.now(),
                        amount = amount,
                        transactionType = TransactionType.EXPENSE,
                        merchant = merchant,
                        notes = null,
                    )

                whenever(mockTransactionService.create(any(), eq(budgetId), eq(budgetItemId)))
                    .thenReturn(transaction)

                webTestClient
                    .post()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId/transactions")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(transactionRequest)
                    .exchange()
                    .expectStatus()
                    .isCreated
                    .expectBody<Transaction>()
                    .isEqualTo(transaction)

                verify(mockTransactionService, times(1)).create(any(), eq(budgetId), eq(budgetItemId))
            }
    }

    @Nested
    inner class UpdateTransaction {
        @Test
        fun `returns 'OK' status with updated transaction`() =
            runTest {
                val transactionRequest =
                    TransactionRequest(
                        date = Instant.now(),
                        amount = amount,
                        transactionType = TransactionType.EXPENSE,
                        merchant = merchant,
                        notes = null,
                    )

                whenever(mockTransactionService.update(eq(transactionId), eq(budgetItemId), eq(budgetId), any()))
                    .thenReturn(transaction)

                webTestClient
                    .put()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId/transactions/$transactionId")
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(transactionRequest)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody<Transaction>()
                    .isEqualTo(transaction)

                verify(mockTransactionService, times(1))
                    .update(eq(transactionId), eq(budgetItemId), eq(budgetId), any())
            }

        @Test
        fun `returns 'NOT FOUND' status when transaction does not exist`() {
            val transactionRequest =
                TransactionRequest(
                    date = Instant.now(),
                    amount = amount,
                    transactionType = TransactionType.EXPENSE,
                    merchant = merchant,
                    notes = null,
                )

            runBlocking {
                whenever(mockTransactionService.update(eq(transactionId), eq(budgetItemId), eq(budgetId), any()))
                    .thenThrow(
                        TransactionDoesNotExistException("Transaction does not exist"),
                    )
            }

            webTestClient
                .put()
                .uri("/api/budgets/$budgetId/items/$budgetItemId/transactions/$transactionId")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange()
                .expectStatus()
                .isNotFound

            runBlocking {
                verify(mockTransactionService, times(1))
                    .update(eq(transactionId), eq(budgetItemId), eq(budgetId), any())
            }
        }
    }

    @Nested
    inner class DeleteTransaction {
        @Test
        fun `returns 'OK' status`() =
            runTest {
                webTestClient
                    .delete()
                    .uri("/api/budgets/$budgetId/items/$budgetItemId/transactions/$transactionId")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk

                verify(mockTransactionService, times(1)).delete(transactionId, budgetItemId, budgetId)
            }
    }

    @BeforeEach
    fun setup() {
        val transactionController = TransactionController(mockTransactionService)
        webTestClient =
            WebTestClient
                .bindToController(transactionController)
                .controllerAdvice(GlobalExceptionHandler())
                .build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockTransactionService = mock<TransactionService>()

    private val budgetId = UUID.randomUUID()
    private val budgetItemId = UUID.randomUUID()
    private val transactionId = UUID.randomUUID()
    private val merchant = "Test merchant"
    private val amount = BigDecimal("100.25")
    private val transaction =
        Transaction(
            id = transactionId,
            date = Instant.now(),
            amount = amount,
            transactionType = TransactionType.EXPENSE,
            merchant = merchant,
            notes = null,
            budgetId = budgetId,
            budgetItemId = budgetItemId,
        )
}

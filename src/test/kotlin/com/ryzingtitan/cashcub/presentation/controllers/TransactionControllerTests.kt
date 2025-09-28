package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import com.ryzingtitan.cashcub.domain.transactions.services.TransactionService
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
import java.time.Instant
import java.util.UUID
import kotlin.test.Test

class TransactionControllerTests {
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

                whenever(mockTransactionService.create(transactionRequest, budgetId, budgetItemId))
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

                verify(mockTransactionService, times(1)).create(transactionRequest, budgetId, budgetItemId)
            }
    }

    @BeforeEach
    fun setup() {
        val transactionController = TransactionController(mockTransactionService)
        webTestClient = WebTestClient.bindToController(transactionController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockTransactionService = mock<TransactionService>()

    private val budgetId = UUID.randomUUID()
    private val budgetItemId = UUID.randomUUID()
    private val merchant = "Test merchant"
    private val amount = BigDecimal("100.25")
    private val transaction =
        Transaction(
            id = UUID.randomUUID(),
            date = Instant.now(),
            amount = amount,
            transactionType = TransactionType.EXPENSE,
            merchant = merchant,
            notes = null,
            budgetId = budgetId,
            budgetItemId = budgetItemId,
        )
}

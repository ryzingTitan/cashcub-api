package com.ryzingtitan.cashcub.domain.transactions.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import com.ryzingtitan.cashcub.domain.transactions.exceptions.TransactionDoesNotExistException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionServiceTests {
    @Nested
    inner class GetAllByBudgetItemIdAndBudgetId {
        @Test
        fun `returns all transactions for a budget item and budget`() =
            runTest {
                whenever(
                    mockTransactionRepository.findAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId),
                ).thenReturn(flowOf(transactionEntity))

                val transactions = transactionService.getAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId)

                verify(mockTransactionRepository, times(1)).findAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId)

                assertEquals(listOf(expectedTransaction), transactions.toList())
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Retrieving all transactions for budget item id $budgetItemId and budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a transaction`() =
            runTest {
                val transactionRequest =
                    TransactionRequest(
                        date = transactionDate,
                        amount = amount,
                        transactionType = TransactionType.EXPENSE,
                        merchant = merchant,
                        notes = null,
                    )

                whenever(mockTransactionRepository.save(transactionEntity.copy(id = null)))
                    .thenReturn(transactionEntity)

                val transaction = transactionService.create(transactionRequest, budgetId, budgetItemId)

                verify(mockTransactionRepository, times(1)).save(transactionEntity.copy(id = null))

                assertEquals(expectedTransaction, transaction)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Creating a transaction for budget item id $budgetItemId and budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Update {
        @Test
        fun `updates an existing transaction`() =
            runTest {
                val updatedMerchant = "Updated merchant"

                val transactionRequest =
                    TransactionRequest(
                        date = transactionDate,
                        amount = amount,
                        transactionType = TransactionType.EXPENSE,
                        merchant = updatedMerchant,
                        notes = null,
                    )

                whenever(mockTransactionRepository.findById(transactionId)).thenReturn(transactionEntity)
                whenever(
                    mockTransactionRepository.save(transactionEntity.copy(merchant = updatedMerchant)),
                ).thenReturn(transactionEntity.copy(merchant = updatedMerchant))

                val transaction = transactionService.update(transactionId, budgetItemId, budgetId, transactionRequest)

                verify(mockTransactionRepository, times(1)).findById(transactionId)
                verify(mockTransactionRepository, times(1)).save(transactionEntity.copy(merchant = updatedMerchant))

                assertEquals(expectedTransaction.copy(merchant = updatedMerchant), transaction)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Updating transaction with id $transactionId", appender.list[0].message)
            }

        @Test
        fun `throws 'TransactionDoesNotExistException' when the transaction does not exist`() =
            runTest {
                val transactionRequest =
                    TransactionRequest(
                        date = transactionDate,
                        amount = amount,
                        transactionType = TransactionType.EXPENSE,
                        merchant = merchant,
                        notes = null,
                    )

                whenever(mockTransactionRepository.findById(transactionId)).thenReturn(null)

                val exception =
                    assertThrows<TransactionDoesNotExistException> {
                        transactionService.update(transactionId, budgetItemId, budgetId, transactionRequest)
                    }

                verify(mockTransactionRepository, times(1)).findById(transactionId)
                verify(mockTransactionRepository, never()).save(any())

                assertEquals(
                    "Transaction does not exist for budget item id $budgetItemId and budget id $budgetId",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "Transaction does not exist for budget item id $budgetItemId and budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Delete {
        @Test
        fun `deletes an existing transaction`() =
            runTest {
                transactionService.delete(transactionId, budgetItemId, budgetId)

                verify(mockTransactionRepository, times(1)).deleteById(transactionId)

                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Deleting transaction with id $transactionId from budget item id $budgetItemId " +
                        "and budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @BeforeEach
    fun setup() {
        transactionService = TransactionService(mockTransactionRepository)

        logger = LoggerFactory.getLogger(TransactionService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var transactionService: TransactionService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockTransactionRepository = mock<TransactionRepository>()

    private val budgetId = UUID.randomUUID()
    private val budgetItemId = UUID.randomUUID()
    private val merchant = "Test merchant"
    private val amount = BigDecimal("100.25")
    private val transactionId = UUID.randomUUID()
    private val transactionDate = Instant.now()

    private val transactionEntity =
        TransactionEntity(
            id = transactionId,
            date = transactionDate,
            amount = amount,
            transactionType = TransactionType.EXPENSE,
            merchant = merchant,
            notes = null,
            budgetId = budgetId,
            budgetItemId = budgetItemId,
        )

    private val expectedTransaction =
        Transaction(
            id = transactionId,
            date = transactionDate,
            amount = amount,
            transactionType = TransactionType.EXPENSE,
            merchant = merchant,
            notes = null,
            budgetId = budgetId,
            budgetItemId = budgetItemId,
        )
}

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
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.mock
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

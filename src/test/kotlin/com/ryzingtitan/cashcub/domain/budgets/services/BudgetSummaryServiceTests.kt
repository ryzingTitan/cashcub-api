package com.ryzingtitan.cashcub.domain.budgets.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.data.categories.entities.CategoryEntity
import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.BudgetDoesNotExistException
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
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
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class BudgetSummaryServiceTests {
    @Nested
    inner class GetBudgetSummary {
        @Test
        fun `returns budget summary`() =
            runTest {
                whenever(mockBudgetRepository.findById(budgetId)).thenReturn(budgetEntity)
                whenever(
                    mockBudgetItemRepository.findAllByBudgetId(budgetId),
                ).thenReturn(flowOf(firstBudgetItemEntity, secondBudgetItemEntity, thirdBudgetItemEntity))
                whenever(
                    mockTransactionRepository.findAllByBudgetItemIdAndBudgetId(firstBudgetItemId, budgetId),
                ).thenReturn(flowOf(firstTransactionEntity, secondTransactionEntity))
                whenever(mockTransactionRepository.findAllByBudgetItemIdAndBudgetId(secondBudgetItemId, budgetId))
                    .thenReturn(emptyFlow())
                whenever(
                    mockTransactionRepository.findAllByBudgetItemIdAndBudgetId(thirdBudgetItemId, budgetId),
                ).thenReturn(flowOf(thirdTransactionEntity))
                whenever(mockCategoryRepository.findByName("Income")).thenReturn(incomeCategoryEntity)

                val budgetSummary = budgetSummaryService.getBudgetSummary(budgetId)

                verify(mockBudgetRepository, times(1)).findById(budgetId)
                verify(mockBudgetItemRepository, times(1)).findAllByBudgetId(budgetId)
                verify(mockTransactionRepository, times(1))
                    .findAllByBudgetItemIdAndBudgetId(firstBudgetItemId, budgetId)
                verify(mockTransactionRepository, times(1))
                    .findAllByBudgetItemIdAndBudgetId(secondBudgetItemId, budgetId)
                verify(mockTransactionRepository, times(1))
                    .findAllByBudgetItemIdAndBudgetId(thirdBudgetItemId, budgetId)
                verify(mockCategoryRepository, times(1)).findByName("Income")

                assertEquals(expectedBudgetSummary, budgetSummary)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving budget summary for budget id $budgetId", appender.list[0].message)
            }

        @Test
        fun `throws 'BudgetDoesNotExistException' when the budget does not exist`() =
            runTest {
                whenever(mockBudgetRepository.findById(budgetId)).thenReturn(null)

                val exception =
                    assertThrows<BudgetDoesNotExistException> {
                        budgetSummaryService.getBudgetSummary(budgetId)
                    }

                verify(mockBudgetRepository, times(1)).findById(budgetId)
                verify(mockBudgetItemRepository, never()).findAllByBudgetId(any())
                verify(mockTransactionRepository, never()).findAllByBudgetItemIdAndBudgetId(any(), any())
                verify(mockCategoryRepository, never()).findByName(any())

                assertEquals("Budget with id $budgetId does not exist", exception.message)
                assertEquals(2, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving budget summary for budget id $budgetId", appender.list[0].message)
                assertEquals(Level.ERROR, appender.list[1].level)
                assertEquals("Budget with id $budgetId does not exist", appender.list[1].message)
            }
    }

    @BeforeEach
    fun setup() {
        budgetSummaryService =
            BudgetSummaryService(
                mockBudgetRepository,
                mockBudgetItemRepository,
                mockTransactionRepository,
                mockCategoryRepository,
            )

        logger = LoggerFactory.getLogger(BudgetSummaryService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var budgetSummaryService: BudgetSummaryService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockBudgetItemRepository = mock<BudgetItemRepository>()
    private val mockTransactionRepository = mock<TransactionRepository>()
    private val mockBudgetRepository = mock<BudgetRepository>()
    private val mockCategoryRepository = mock<CategoryRepository>()

    private val budgetId = UUID.randomUUID()
    private val month = 10
    private val year = 2025
    private val firstCategoryId = UUID.randomUUID()
    private val secondCategoryId = UUID.randomUUID()
    private val thirdCategoryId = UUID.randomUUID()
    private val firstBudgetItemId = UUID.randomUUID()
    private val firstBudgetItemName = "First Budget Item"
    private val firstBudgetItemPlannedAmount = BigDecimal("100.00")
    private val secondBudgetItemId = UUID.randomUUID()
    private val secondBudgetItemName = "Second Budget Item"
    private val secondBudgetItemPlannedAmount = BigDecimal("50.00")
    private val thirdBudgetItemId = UUID.randomUUID()
    private val thirdBudgetItemName = "Third Budget Item"
    private val thirdBudgetItemPlannedAmount = BigDecimal("1000.00")
    private val firstTransactionAmount = BigDecimal("30.00")
    private val secondTransactionAmount = BigDecimal("20.50")
    private val thirdTransactionAmount = BigDecimal("500.00")

    private val budgetEntity =
        BudgetEntity(
            id = budgetId,
            budgetMonth = month,
            budgetYear = year,
        )

    private val firstBudgetItemEntity =
        BudgetItemEntity(
            id = firstBudgetItemId,
            name = firstBudgetItemName,
            plannedAmount = firstBudgetItemPlannedAmount,
            budgetId = budgetId,
            categoryId = firstCategoryId,
        )
    private val secondBudgetItemEntity =
        BudgetItemEntity(
            id = secondBudgetItemId,
            name = secondBudgetItemName,
            plannedAmount = secondBudgetItemPlannedAmount,
            budgetId = budgetId,
            categoryId = secondCategoryId,
        )

    private val thirdBudgetItemEntity =
        BudgetItemEntity(
            id = thirdBudgetItemId,
            name = thirdBudgetItemName,
            plannedAmount = thirdBudgetItemPlannedAmount,
            budgetId = budgetId,
            categoryId = thirdCategoryId,
        )

    private val firstTransactionEntity =
        TransactionEntity(
            id = UUID.randomUUID(),
            date = Instant.now().minus(5, ChronoUnit.DAYS),
            amount = firstTransactionAmount,
            transactionType = TransactionType.EXPENSE,
            budgetId = budgetId,
            budgetItemId = firstBudgetItemId,
        )

    private val secondTransactionEntity =
        TransactionEntity(
            id = UUID.randomUUID(),
            date = Instant.now().minus(10, ChronoUnit.DAYS),
            amount = secondTransactionAmount,
            transactionType = TransactionType.EXPENSE,
            budgetId = budgetId,
            budgetItemId = firstBudgetItemId,
        )

    private val thirdTransactionEntity =
        TransactionEntity(
            id = UUID.randomUUID(),
            date = Instant.now().minus(15, ChronoUnit.DAYS),
            amount = thirdTransactionAmount,
            transactionType = TransactionType.INCOME,
            budgetId = budgetId,
            budgetItemId = thirdBudgetItemId,
        )

    private val incomeCategoryEntity =
        CategoryEntity(
            id = thirdCategoryId,
            name = "Income",
        )

    private val firstBudgetItem =
        BudgetItem(
            id = firstBudgetItemId,
            name = firstBudgetItemName,
            plannedAmount = firstBudgetItemPlannedAmount,
            actualAmount = BigDecimal("50.50"),
            budgetId = budgetId,
            categoryId = firstCategoryId,
        )

    private val secondBudgetItem =
        BudgetItem(
            id = secondBudgetItemId,
            name = secondBudgetItemName,
            plannedAmount = secondBudgetItemPlannedAmount,
            actualAmount = BigDecimal("0.00"),
            budgetId = budgetId,
            categoryId = secondCategoryId,
        )

    private val thirdBudgetItem =
        BudgetItem(
            id = thirdBudgetItemId,
            name = thirdBudgetItemName,
            plannedAmount = thirdBudgetItemPlannedAmount,
            actualAmount = BigDecimal("500.00"),
            budgetId = budgetId,
            categoryId = thirdCategoryId,
        )

    private val expectedBudgetSummary =
        BudgetSummary(
            id = budgetId,
            month = month,
            year = year,
            expectedIncome = thirdBudgetItemPlannedAmount,
            actualIncome = thirdTransactionAmount,
            expectedExpenses = firstBudgetItemPlannedAmount + secondBudgetItemPlannedAmount,
            actualExpenses = firstTransactionAmount + secondTransactionAmount,
            budgetItems = listOf(firstBudgetItem, secondBudgetItem, thirdBudgetItem),
        )
}

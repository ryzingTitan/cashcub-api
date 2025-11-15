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
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetRequest
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.DuplicateBudgetException
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
import java.util.UUID
import kotlin.jvm.java
import kotlin.test.Test
import kotlin.test.assertEquals

class BudgetServiceTests {
    @Nested
    inner class GetAll {
        @Test
        fun `returns all budgets`() =
            runTest {
                whenever(mockBudgetRepository.findAll()).thenReturn(flowOf(firstBudgetEntity, secondBudgetEntity))

                val budgets = budgetService.getAll()

                verify(mockBudgetRepository, times(1)).findAll()

                assertEquals(listOf(firstBudget, secondBudget), budgets.toList())
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving all budgets", appender.list[0].message)
            }
    }

    @Nested
    inner class GetAllForRange {
        @Test
        fun `returns all budgets for date range`() =
            runTest {
                whenever(
                    mockBudgetRepository.findAllByBudgetMonthBetweenAndBudgetYearBetween(9, 10, 2025, 2025),
                ).thenReturn(flowOf(firstBudgetEntity, secondBudgetEntity))

                val budgets = budgetService.getAllForRange("9-2025", "10-2025")

                verify(mockBudgetRepository, times(1))
                    .findAllByBudgetMonthBetweenAndBudgetYearBetween(9, 10, 2025, 2025)

                assertEquals(listOf(firstBudget, secondBudget), budgets.toList())
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving all budgets from 9-2025 to 10-2025", appender.list[0].message)
            }
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a new budget`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        budgetRequest.month,
                        budgetRequest.year,
                    ),
                ).thenReturn(null)
                whenever(mockBudgetRepository.save(firstBudgetEntity.copy(id = null))).thenReturn(firstBudgetEntity)

                val budget = budgetService.create(budgetRequest)

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)
                verify(mockBudgetRepository, times(1)).save(firstBudgetEntity.copy(id = null))

                assertEquals(firstBudget, budget)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Creating a budget for month $firstBudgetMonth and year $firstBudgetYear",
                    appender.list[0].message,
                )
            }

        @Test
        fun `throws 'DuplicateBudgetException' when a budget with the same month and year already exists`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        budgetRequest.month,
                        budgetRequest.year,
                    ),
                ).thenReturn(firstBudgetEntity)

                val exception =
                    assertThrows<DuplicateBudgetException> {
                        budgetService.create(budgetRequest)
                    }

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)
                verify(mockBudgetRepository, never()).save(any())

                assertEquals(
                    "Budget already exists for month $firstBudgetMonth and year $firstBudgetYear",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "Budget already exists for month $firstBudgetMonth and year $firstBudgetYear",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Clone {
        @Test
        fun `clones a budget from an existing budget`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        budgetRequest.month,
                        budgetRequest.year,
                    ),
                ).thenReturn(null)
                whenever(mockBudgetRepository.save(firstBudgetEntity.copy(id = null))).thenReturn(firstBudgetEntity)
                whenever(mockBudgetItemRepository.findAllByBudgetId(secondBudgetId))
                    .thenReturn(flowOf(budgetItemEntity))
                whenever(mockBudgetSummaryService.getBudgetSummary(firstBudgetId)).thenReturn(expectedBudgetSummary)

                val budgetSummary = budgetService.clone(secondBudgetId, budgetRequest)

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)
                verify(mockBudgetRepository, times(1)).save(firstBudgetEntity.copy(id = null))
                verify(mockBudgetItemRepository, times(1)).findAllByBudgetId(secondBudgetId)
                verify(mockBudgetItemRepository, times(1))
                    .save(budgetItemEntity.copy(id = null, budgetId = firstBudgetId))
                verify(mockBudgetSummaryService, times(1)).getBudgetSummary(firstBudgetId)

                assertEquals(expectedBudgetSummary, budgetSummary)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Cloning budget id $secondBudgetId for month ${budgetRequest.month} and year ${budgetRequest.year}",
                    appender.list[0].message,
                )
            }

        @Test
        fun `throws 'DuplicateBudgetException' when a budget with the same month and year already exists`() =
            runTest {
                val budgetRequest = BudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        budgetRequest.month,
                        budgetRequest.year,
                    ),
                ).thenReturn(firstBudgetEntity)

                val exception =
                    assertThrows<DuplicateBudgetException> {
                        budgetService.clone(firstBudgetId, budgetRequest)
                    }

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)
                verify(mockBudgetRepository, never()).save(any())
                verify(mockBudgetItemRepository, never()).findAllByBudgetId(any())
                verify(mockBudgetItemRepository, never()).save(any())
                verify(mockBudgetSummaryService, never()).getBudgetSummary(any())

                assertEquals(
                    "Budget already exists for month $firstBudgetMonth and year $firstBudgetYear",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "Budget already exists for month $firstBudgetMonth and year $firstBudgetYear",
                    appender.list[0].message,
                )
            }
    }

    @BeforeEach
    fun setup() {
        budgetService = BudgetService(mockBudgetRepository, mockBudgetItemRepository, mockBudgetSummaryService)

        logger = LoggerFactory.getLogger(BudgetService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var budgetService: BudgetService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockBudgetRepository = mock<BudgetRepository>()
    private val mockBudgetItemRepository = mock<BudgetItemRepository>()
    private val mockBudgetSummaryService = mock<BudgetSummaryService>()

    private val firstBudgetId = UUID.randomUUID()
    private val secondBudgetId = UUID.randomUUID()
    private val firstBudgetMonth = 9
    private val secondBudgetMonth = 10
    private val firstBudgetYear = 2025
    private val secondBudgetYear = 2025

    private val firstBudgetEntity =
        BudgetEntity(
            id = firstBudgetId,
            budgetMonth = firstBudgetMonth,
            budgetYear = firstBudgetYear,
        )

    private val secondBudgetEntity =
        BudgetEntity(
            id = secondBudgetId,
            budgetMonth = secondBudgetMonth,
            budgetYear = secondBudgetYear,
        )

    private val budgetItemEntity =
        BudgetItemEntity(
            id = UUID.randomUUID(),
            name = "Test Budget Item",
            plannedAmount = BigDecimal("100.25"),
            budgetId = secondBudgetId,
            categoryId = UUID.randomUUID(),
        )

    private val firstBudget =
        Budget(
            id = firstBudgetId,
            month = firstBudgetMonth,
            year = firstBudgetYear,
        )

    private val secondBudget =
        Budget(
            id = secondBudgetId,
            month = secondBudgetMonth,
            year = secondBudgetYear,
        )

    private val expectedBudgetSummary =
        BudgetSummary(
            id = firstBudgetId,
            month = 10,
            year = 2025,
            expectedIncome = BigDecimal("10000.25"),
            actualIncome = BigDecimal("500.00"),
            expectedExpenses = BigDecimal("800.50"),
            actualExpenses = BigDecimal("100.25"),
            budgetItems = emptyList(),
        )
}

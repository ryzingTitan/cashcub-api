package com.ryzingtitan.cashcub.domain.budgets.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.CreateBudgetRequest
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
    inner class Create {
        @Test
        fun `creates a new budget`() =
            runTest {
                val createBudgetRequest = CreateBudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        createBudgetRequest.month,
                        createBudgetRequest.year,
                    ),
                ).thenReturn(null)
                whenever(mockBudgetRepository.save(firstBudgetEntity.copy(id = null))).thenReturn(firstBudgetEntity)

                val budget = budgetService.create(createBudgetRequest)

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(createBudgetRequest.month, createBudgetRequest.year)
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
        fun `throws 'DuplicateBudgetException' a budget with the same month and year already exists`() =
            runTest {
                val createBudgetRequest = CreateBudgetRequest(month = 9, year = 2025)

                whenever(
                    mockBudgetRepository.findByBudgetMonthAndBudgetYear(
                        createBudgetRequest.month,
                        createBudgetRequest.year,
                    ),
                ).thenReturn(firstBudgetEntity)

                val exception =
                    assertThrows<DuplicateBudgetException> {
                        budgetService.create(createBudgetRequest)
                    }

                verify(
                    mockBudgetRepository,
                    times(1),
                ).findByBudgetMonthAndBudgetYear(createBudgetRequest.month, createBudgetRequest.year)
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

    @BeforeEach
    fun setup() {
        budgetService = BudgetService(mockBudgetRepository)

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

    private val firstBudget =
        Budget(
            id = firstBudgetId,
            month = firstBudgetMonth,
            year = firstBudgetYear,
        )

    private val secondBudgetEntity =
        BudgetEntity(
            id = secondBudgetId,
            budgetMonth = secondBudgetMonth,
            budgetYear = secondBudgetYear,
        )

    private val secondBudget =
        Budget(
            id = secondBudgetId,
            month = secondBudgetMonth,
            year = secondBudgetYear,
        )
}

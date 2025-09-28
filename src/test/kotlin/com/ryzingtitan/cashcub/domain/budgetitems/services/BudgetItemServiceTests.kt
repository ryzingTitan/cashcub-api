package com.ryzingtitan.cashcub.domain.budgetitems.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.BudgetItemDoesNotExistException
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
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

class BudgetItemServiceTests {
    @Nested
    inner class GetAllByBudgetId {
        @Test
        fun `returns all budget items for a budget`() =
            runTest {
                whenever(mockBudgetItemRepository.findAllByBudgetId(budgetId)).thenReturn(flowOf(budgetItemEntity))

                val budgetItems = budgetItemService.getAllByBudgetId(budgetId)

                assertEquals(listOf(expectedBudgetItem), budgetItems.toList())
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving all budget items for budget id $budgetId", appender.list[0].message)
            }
    }

    @Nested
    inner class Create {
        @Test
        fun `creates a new budget item`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(
                    mockBudgetItemRepository.findByNameAndBudgetId(
                        name,
                        budgetId,
                    ),
                ).thenReturn(null)
                whenever(mockBudgetItemRepository.save(budgetItemEntity.copy(id = null))).thenReturn(budgetItemEntity)

                val budgetItem = budgetItemService.create(budgetItemRequest, budgetId)

                verify(
                    mockBudgetItemRepository,
                    times(1),
                ).findByNameAndBudgetId(budgetItemRequest.name, budgetId)
                verify(mockBudgetItemRepository, times(1)).save(budgetItemEntity.copy(id = null))

                assertEquals(expectedBudgetItem, budgetItem)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Creating a budget item with name $name for budget id $budgetId",
                    appender.list[0].message,
                )
            }

        @Test
        fun `throws 'DuplicateBudgetItemException' when budget item with the same name and budget id already exists`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(
                    mockBudgetItemRepository.findByNameAndBudgetId(
                        name,
                        budgetId,
                    ),
                ).thenReturn(budgetItemEntity)

                val exception =
                    assertThrows<DuplicateBudgetItemException> {
                        budgetItemService.create(budgetItemRequest, budgetId)
                    }

                verify(
                    mockBudgetItemRepository,
                    times(1),
                ).findByNameAndBudgetId(name, budgetId)
                verify(mockBudgetItemRepository, never()).save(any())

                assertEquals(
                    "Budget item already exists for name $name and budget id $budgetId",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "Budget item already exists for name $name and budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Update {
        @Test
        fun `updates an existing budget item`() =
            runTest {
                val updatedName = "Updated budget item"

                val budgetItemRequest =
                    BudgetItemRequest(
                        name = updatedName,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemRepository.findById(budgetItemId)).thenReturn(budgetItemEntity)
                whenever(
                    mockBudgetItemRepository.save(budgetItemEntity.copy(name = updatedName)),
                ).thenReturn(budgetItemEntity.copy(name = updatedName))

                val budgetItem = budgetItemService.update(budgetItemId, budgetId, budgetItemRequest)

                verify(mockBudgetItemRepository, times(1)).findById(budgetItemId)
                verify(mockBudgetItemRepository, times(1)).save(budgetItemEntity.copy(name = updatedName))

                assertEquals(expectedBudgetItem.copy(name = updatedName), budgetItem)
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Updating budget item with id $budgetItemId", appender.list[0].message)
            }

        @Test
        fun `throws 'BudgetItemDoesNotExistException' when the budget item does not exist`() =
            runTest {
                val budgetItemRequest =
                    BudgetItemRequest(
                        name = name,
                        plannedAmount = plannedAmount,
                        categoryId = categoryId,
                    )

                whenever(mockBudgetItemRepository.findById(budgetItemId)).thenReturn(null)

                val exception =
                    assertThrows<BudgetItemDoesNotExistException> {
                        budgetItemService.update(budgetItemId, budgetId, budgetItemRequest)
                    }

                verify(mockBudgetItemRepository, times(1)).findById(budgetItemId)
                verify(mockBudgetItemRepository, never()).save(any())

                assertEquals(
                    "Budget item with name $name does not exist for budget id $budgetId",
                    exception.message,
                )
                assertEquals(1, appender.list.size)
                assertEquals(Level.ERROR, appender.list[0].level)
                assertEquals(
                    "Budget item with name $name does not exist for budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @Nested
    inner class Delete {
        @Test
        fun `deletes an existing budget item`() =
            runTest {
                budgetItemService.delete(budgetItemId, budgetId)

                verify(mockBudgetItemRepository, times(1)).deleteById(budgetItemId)

                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals(
                    "Deleting budget item with id $budgetItemId from budget id $budgetId",
                    appender.list[0].message,
                )
            }
    }

    @BeforeEach
    fun setup() {
        budgetItemService = BudgetItemService(mockBudgetItemRepository)

        logger = LoggerFactory.getLogger(BudgetItemService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var budgetItemService: BudgetItemService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockBudgetItemRepository = mock<BudgetItemRepository>()

    private val budgetId = UUID.randomUUID()
    private val budgetItemId = UUID.randomUUID()
    private val name = "Budget Item"
    private val plannedAmount = BigDecimal("100.25")
    private val categoryId = UUID.randomUUID()

    private val budgetItemEntity =
        BudgetItemEntity(
            id = budgetItemId,
            name = name,
            plannedAmount = plannedAmount,
            budgetId = budgetId,
            categoryId = categoryId,
        )

    private val expectedBudgetItem =
        BudgetItem(
            id = budgetItemId,
            name = name,
            plannedAmount = plannedAmount,
            budgetId = budgetId,
            categoryId = categoryId,
        )
}

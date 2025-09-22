package com.ryzingtitan.cashcub.domain.categories.services

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.data.categories.entities.CategoryEntity
import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import com.ryzingtitan.cashcub.domain.categories.dtos.Category
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.jvm.java
import kotlin.test.assertEquals

class CategoryServiceTests {
    @Nested
    inner class GetAll {
        @Test
        fun `returns all categories`() =
            runTest {
                whenever(mockCategoryRepository.findAll()).thenReturn(flowOf(firstCategoryEntity, secondCategoryEntity))

                val categories = categoryService.getAll()

                assertEquals(listOf(firstCategory, secondCategory), categories.toList())
                assertEquals(1, appender.list.size)
                assertEquals(Level.INFO, appender.list[0].level)
                assertEquals("Retrieving all categories", appender.list[0].message)
            }
    }

    @BeforeEach
    fun setup() {
        categoryService = CategoryService(mockCategoryRepository)

        logger = LoggerFactory.getLogger(CategoryService::class.java) as Logger
        appender = ListAppender()
        appender.context = LoggerContext()
        logger.addAppender(appender)
        appender.start()
    }

    private lateinit var categoryService: CategoryService
    private lateinit var logger: Logger
    private lateinit var appender: ListAppender<ILoggingEvent>

    private val mockCategoryRepository = mock<CategoryRepository>()

    private val firstCategoryId = UUID.randomUUID()
    private val secondCategoryId = UUID.randomUUID()
    private val firstCategoryName = "First Category"
    private val secondCategoryName = "Second Category"

    private val firstCategoryEntity =
        CategoryEntity(
            id = firstCategoryId,
            name = firstCategoryName,
        )

    private val firstCategory =
        Category(
            id = firstCategoryId,
            name = firstCategoryName,
        )

    private val secondCategoryEntity =
        CategoryEntity(
            id = secondCategoryId,
            name = secondCategoryName,
        )

    private val secondCategory =
        Category(
            id = secondCategoryId,
            name = secondCategoryName,
        )
}

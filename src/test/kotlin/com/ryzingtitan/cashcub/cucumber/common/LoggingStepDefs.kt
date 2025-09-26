package com.ryzingtitan.cashcub.cucumber.common

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.ryzingtitan.cashcub.cucumber.dtos.LogMessage
import com.ryzingtitan.cashcub.domain.budgetitems.services.BudgetItemService
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import com.ryzingtitan.cashcub.domain.categories.services.CategoryService
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import org.slf4j.LoggerFactory
import kotlin.collections.forEach
import kotlin.jvm.java
import kotlin.test.assertEquals
import kotlin.text.orEmpty

class LoggingStepDefs {
    @Then("the application will log the following messages:")
    fun theApplicationWilLogTheFollowingMessages(expectedLogMessages: List<LogMessage>) {
        val actualLogMessages = mutableListOf<LogMessage>()

        appender.list.forEach {
            actualLogMessages.add(LogMessage(it.level.levelStr, it.message))
        }

        assertEquals(expectedLogMessages, actualLogMessages)
    }

    @DataTableType
    fun mapLogMessage(tableRow: Map<String, String>): LogMessage =
        LogMessage(
            level = tableRow["level"].orEmpty(),
            message = tableRow["message"].orEmpty(),
        )

    @Before
    fun setup() {
        categoryServiceLogger = LoggerFactory.getLogger(CategoryService::class.java) as Logger
        categoryServiceLogger.addAppender(appender)

        budgetServiceLogger = LoggerFactory.getLogger(BudgetService::class.java) as Logger
        budgetServiceLogger.addAppender(appender)

        budgetItemServiceLogger = LoggerFactory.getLogger(BudgetItemService::class.java) as Logger
        budgetItemServiceLogger.addAppender(appender)

        appender.context = LoggerContext()
        appender.start()
    }

    @After
    fun teardown() {
        appender.stop()
    }

    private lateinit var categoryServiceLogger: Logger
    private lateinit var budgetServiceLogger: Logger
    private lateinit var budgetItemServiceLogger: Logger

    private val appender: ListAppender<ILoggingEvent> = ListAppender()
}

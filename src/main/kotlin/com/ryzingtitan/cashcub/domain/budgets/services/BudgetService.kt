package com.ryzingtitan.cashcub.domain.budgets.services

import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetRequest
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.DuplicateBudgetException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.Throws

@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
    private val budgetItemRepository: BudgetItemRepository,
    private val budgetSummaryService: BudgetSummaryService,
) {
    fun getAll(): Flow<Budget> {
        logger.info("Retrieving all budgets")

        return budgetRepository.findAll().map {
            Budget(
                id = it.id!!,
                month = it.budgetMonth,
                year = it.budgetYear,
            )
        }
    }

    suspend fun getAllForRange(
        startDate: String,
        endDate: String,
    ): Flow<Budget> {
        logger.info("Retrieving all budgets from $startDate to $endDate")

        val startMonth = startDate.split("-")[0].toInt()
        val startYear = startDate.split("-")[1].toInt()
        val endMonth = endDate.split("-")[0].toInt()
        val endYear = endDate.split("-")[1].toInt()

        return budgetRepository
            .findAllByBudgetMonthBetweenAndBudgetYearBetween(startMonth, endMonth, startYear, endYear)
            .map {
                Budget(
                    id = it.id!!,
                    month = it.budgetMonth,
                    year = it.budgetYear,
                )
            }
    }

    @Throws(DuplicateBudgetException::class)
    suspend fun create(budgetRequest: BudgetRequest): Budget {
        val existingBudget =
            budgetRepository.findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)

        if (existingBudget != null) {
            val message =
                "Budget already exists for month ${budgetRequest.month} and year ${budgetRequest.year}"
            logger.error(message)
            throw DuplicateBudgetException(message)
        }

        logger.info("Creating a budget for month ${budgetRequest.month} and year ${budgetRequest.year}")

        val createdBudget =
            budgetRepository.save(
                BudgetEntity(
                    budgetMonth = budgetRequest.month,
                    budgetYear = budgetRequest.year,
                ),
            )

        return Budget(
            id = createdBudget.id!!,
            month = createdBudget.budgetMonth,
            year = createdBudget.budgetYear,
        )
    }

    @Throws(DuplicateBudgetException::class)
    suspend fun clone(
        budgetId: UUID,
        budgetRequest: BudgetRequest,
    ): BudgetSummary {
        val existingBudget =
            budgetRepository.findByBudgetMonthAndBudgetYear(budgetRequest.month, budgetRequest.year)

        if (existingBudget != null) {
            val message =
                "Budget already exists for month ${budgetRequest.month} and year ${budgetRequest.year}"
            logger.error(message)
            throw DuplicateBudgetException(message)
        }

        logger.info("Cloning budget id $budgetId for month ${budgetRequest.month} and year ${budgetRequest.year}")

        val createdBudget =
            budgetRepository.save(
                BudgetEntity(
                    budgetMonth = budgetRequest.month,
                    budgetYear = budgetRequest.year,
                ),
            )

        budgetItemRepository.findAllByBudgetId(budgetId).collect {
            budgetItemRepository.save(
                it.copy(
                    id = null,
                    budgetId = createdBudget.id!!,
                ),
            )
        }

        return budgetSummaryService.getBudgetSummary(createdBudget.id!!)
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetService::class.java)
}

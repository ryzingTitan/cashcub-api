package com.ryzingtitan.cashcub.domain.budgets.services

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.domain.budgets.DuplicateBudgetException
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.CreateBudgetRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class BudgetService(
    private val budgetRepository: BudgetRepository,
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

    @Throws(DuplicateBudgetException::class)
    suspend fun create(createBudgetRequest: CreateBudgetRequest): Budget {
        val existingBudget =
            budgetRepository.findBudgetByBudgetMonthAndBudgetYear(createBudgetRequest.month, createBudgetRequest.year)

        if (existingBudget != null) {
            val message =
                "Budget already exists for month ${createBudgetRequest.month} and year ${createBudgetRequest.year}"
            logger.error(message)
            throw DuplicateBudgetException(message)
        }

        logger.info("Creating a budget for month ${createBudgetRequest.month} and year ${createBudgetRequest.year}")

        val createdBudget =
            budgetRepository.save(
                BudgetEntity(
                    budgetMonth = createBudgetRequest.month,
                    budgetYear = createBudgetRequest.year,
                ),
            )

        return Budget(
            id = createdBudget.id!!,
            month = createdBudget.budgetMonth,
            year = createdBudget.budgetYear,
        )
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetService::class.java)
}

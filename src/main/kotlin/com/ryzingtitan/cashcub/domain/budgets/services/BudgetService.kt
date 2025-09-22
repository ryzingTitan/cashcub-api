package com.ryzingtitan.cashcub.domain.budgets.services

import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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

    private val logger: Logger = LoggerFactory.getLogger(BudgetService::class.java)
}

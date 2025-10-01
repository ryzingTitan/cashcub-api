package com.ryzingtitan.cashcub.domain.budgets.services

import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.exceptions.BudgetDoesNotExistException
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BudgetSummaryService(
    private val budgetRepository: BudgetRepository,
    private val budgetItemRepository: BudgetItemRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    @Throws(BudgetDoesNotExistException::class)
    suspend fun getBudgetSummary(budgetId: UUID): BudgetSummary {
        logger.info("Retrieving budget summary for budget id $budgetId")

        val budget = budgetRepository.findById(budgetId)

        if (budget == null) {
            val message = "Budget with id $budgetId does not exist"
            logger.error(message)
            throw BudgetDoesNotExistException(message)
        }

        val budgetItemEntities = budgetItemRepository.findAllByBudgetId(budgetId).toList()
        val transactionEntities =
            budgetItemEntities.flatMap { budgetItemEntity ->
                transactionRepository.findAllByBudgetItemIdAndBudgetId(budgetItemEntity.id!!, budgetId).toList()
            }
        val incomeCategory = categoryRepository.findByName("Income")

        val budgetItems =
            budgetItemEntities
                .map { budgetItemEntity ->
                    BudgetItem(
                        id = budgetItemEntity.id!!,
                        name = budgetItemEntity.name,
                        plannedAmount = budgetItemEntity.plannedAmount.setScale(2),
                        actualAmount =
                            transactionEntities
                                .filter { it.budgetItemId == budgetItemEntity.id }
                                .sumOf { it.amount }
                                .setScale(2),
                        budgetId = budgetId,
                        categoryId = budgetItemEntity.categoryId,
                    )
                }

        return BudgetSummary(
            id = budgetId,
            month = budget.budgetMonth,
            year = budget.budgetYear,
            expectedIncome =
                budgetItems
                    .filter { it.categoryId == incomeCategory?.id }
                    .sumOf { it.plannedAmount }
                    .setScale(2),
            actualIncome =
                transactionEntities
                    .filter { it.transactionType == TransactionType.INCOME }
                    .sumOf { it.amount }
                    .setScale(2),
            expectedExpenses =
                budgetItems
                    .filter { it.categoryId != incomeCategory?.id }
                    .sumOf { it.plannedAmount }
                    .setScale(2),
            actualExpenses =
                transactionEntities
                    .filter { it.transactionType == TransactionType.EXPENSE }
                    .sumOf { it.amount }
                    .setScale(2),
            budgetItems = budgetItems,
        )
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetSummaryService::class.java)
}

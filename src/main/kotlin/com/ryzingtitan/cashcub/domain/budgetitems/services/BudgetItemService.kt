package com.ryzingtitan.cashcub.domain.budgetitems.services

import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.BudgetItemDoesNotExistException
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class BudgetItemService(
    private val budgetItemRepository: BudgetItemRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun getAllByBudgetId(budgetId: UUID): Flow<BudgetItem> {
        logger.info("Retrieving all budget items for budget id $budgetId")

        return budgetItemRepository.findAllByBudgetId(budgetId).map {
            BudgetItem(
                id = it.id!!,
                name = it.name,
                plannedAmount = it.plannedAmount.setScale(2),
                actualAmount = BigDecimal("0.00"),
                budgetId = it.budgetId,
                categoryId = it.categoryId,
            )
        }
    }

    @Throws(DuplicateBudgetItemException::class)
    suspend fun create(
        budgetItemRequest: BudgetItemRequest,
        budgetId: UUID,
    ): BudgetItem {
        val existingBudgetItem =
            budgetItemRepository.findByNameAndBudgetId(budgetItemRequest.name, budgetId)

        if (existingBudgetItem != null) {
            val message =
                "Budget item already exists for name ${budgetItemRequest.name} and budget id $budgetId"
            logger.error(message)
            throw DuplicateBudgetItemException(message)
        }

        logger.info("Creating a budget item with name ${budgetItemRequest.name} for budget id $budgetId")

        val createdBudgetItem =
            budgetItemRepository.save(
                BudgetItemEntity(
                    name = budgetItemRequest.name,
                    plannedAmount = budgetItemRequest.plannedAmount,
                    budgetId = budgetId,
                    categoryId = budgetItemRequest.categoryId,
                ),
            )

        return BudgetItem(
            id = createdBudgetItem.id!!,
            name = createdBudgetItem.name,
            plannedAmount = createdBudgetItem.plannedAmount.setScale(2),
            actualAmount = BigDecimal("0.00"),
            budgetId = createdBudgetItem.budgetId,
            categoryId = createdBudgetItem.categoryId,
        )
    }

    @Throws(BudgetItemDoesNotExistException::class)
    suspend fun update(
        budgetItemId: UUID,
        budgetId: UUID,
        budgetItemRequest: BudgetItemRequest,
    ): BudgetItem {
        val existingBudgetItem = budgetItemRepository.findById(budgetItemId)

        if (existingBudgetItem == null) {
            val message = "Budget item with name ${budgetItemRequest.name} does not exist for budget id $budgetId"
            logger.error(message)
            throw BudgetItemDoesNotExistException(message)
        }

        logger.info("Updating budget item with id $budgetItemId")

        val updatedBudgetItemEntity =
            budgetItemRepository.save(
                BudgetItemEntity(
                    id = budgetItemId,
                    name = budgetItemRequest.name,
                    plannedAmount = budgetItemRequest.plannedAmount,
                    budgetId = budgetId,
                    categoryId = budgetItemRequest.categoryId,
                ),
            )

        return BudgetItem(
            id = updatedBudgetItemEntity.id!!,
            name = updatedBudgetItemEntity.name,
            plannedAmount = updatedBudgetItemEntity.plannedAmount,
            actualAmount = BigDecimal("0.00"),
            budgetId = updatedBudgetItemEntity.budgetId,
            categoryId = updatedBudgetItemEntity.categoryId,
        )
    }

    suspend fun delete(
        budgetItemId: UUID,
        budgetId: UUID,
    ) {
        logger.info("Deleting budget item with id $budgetItemId from budget id $budgetId")
        transactionRepository.deleteAllByBudgetItemId(budgetItemId)
        budgetItemRepository.deleteById(budgetItemId)
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetItemService::class.java)
}

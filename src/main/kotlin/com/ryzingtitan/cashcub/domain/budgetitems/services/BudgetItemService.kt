package com.ryzingtitan.cashcub.domain.budgetitems.services

import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.CreateBudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BudgetItemService(
    private val budgetItemRepository: BudgetItemRepository,
) {
    suspend fun getAllByBudgetId(budgetId: UUID): Flow<BudgetItem> {
        logger.info("Retrieving all budget items for budget id $budgetId")

        return budgetItemRepository.findAllByBudgetId(budgetId).map {
            BudgetItem(
                id = it.id!!,
                name = it.name,
                plannedAmount = it.plannedAmount.setScale(2),
                budgetId = it.budgetId,
                categoryId = it.categoryId,
            )
        }
    }

    @Throws(DuplicateBudgetItemException::class)
    suspend fun create(
        createBudgetItemRequest: CreateBudgetItemRequest,
        budgetId: UUID,
    ): BudgetItem {
        val existingBudgetItem =
            budgetItemRepository.findByNameAndBudgetId(createBudgetItemRequest.name, budgetId)

        if (existingBudgetItem != null) {
            val message =
                "Budget item already exists for name ${createBudgetItemRequest.name} and budget id $budgetId"
            logger.error(message)
            throw DuplicateBudgetItemException(message)
        }

        logger.info("Creating a budget item with name ${createBudgetItemRequest.name} for budget id $budgetId")

        val createdBudgetItem =
            budgetItemRepository.save(
                BudgetItemEntity(
                    name = createBudgetItemRequest.name,
                    plannedAmount = createBudgetItemRequest.plannedAmount,
                    budgetId = budgetId,
                    categoryId = createBudgetItemRequest.categoryId,
                ),
            )

        return BudgetItem(
            id = createdBudgetItem.id!!,
            name = createdBudgetItem.name,
            plannedAmount = createdBudgetItem.plannedAmount.setScale(2),
            budgetId = createdBudgetItem.budgetId,
            categoryId = createdBudgetItem.categoryId,
        )
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetItemService::class.java)
}

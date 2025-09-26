package com.ryzingtitan.cashcub.domain.budgetitems.services

import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.CreateBudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.exceptions.DuplicateBudgetItemException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BudgetItemService(
    private val budgetItemRepository: BudgetItemRepository,
) {
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
            plannedAmount = createdBudgetItem.plannedAmount,
            budgetId = createdBudgetItem.budgetId,
            categoryId = createdBudgetItem.categoryId,
        )
    }

    private val logger: Logger = LoggerFactory.getLogger(BudgetItemService::class.java)
}

package com.ryzingtitan.cashcub.data.budgetitems.repositories

import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface BudgetItemRepository : CoroutineCrudRepository<BudgetItemEntity, UUID> {
    suspend fun findByNameAndBudgetId(
        name: String,
        budgetId: UUID,
    ): BudgetItemEntity?
}

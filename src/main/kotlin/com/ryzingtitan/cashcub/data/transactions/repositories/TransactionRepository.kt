package com.ryzingtitan.cashcub.data.transactions.repositories

import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface TransactionRepository : CoroutineCrudRepository<TransactionEntity, UUID> {
    suspend fun findAllByBudgetItemIdAndBudgetId(
        budgetItemId: UUID,
        budgetId: UUID,
    ): Flow<TransactionEntity>
}

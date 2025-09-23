package com.ryzingtitan.cashcub.data.budgets.repositories

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface BudgetRepository : CoroutineCrudRepository<BudgetEntity, UUID> {
    suspend fun findBudgetByBudgetMonthAndBudgetYear(
        month: Int,
        year: Int,
    ): BudgetEntity?
}

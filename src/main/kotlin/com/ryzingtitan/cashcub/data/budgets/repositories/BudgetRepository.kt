package com.ryzingtitan.cashcub.data.budgets.repositories

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface BudgetRepository : CoroutineCrudRepository<BudgetEntity, UUID> {
    suspend fun findByBudgetMonthAndBudgetYear(
        month: Int,
        year: Int,
    ): BudgetEntity?

    suspend fun findAllByBudgetMonthBetweenAndBudgetYearBetween(
        startMonth: Int,
        endMonth: Int,
        startYear: Int,
        endYear: Int,
    ): Flow<BudgetEntity>
}

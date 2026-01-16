package com.ryzingtitan.cashcub.data.budgets.repositories

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface BudgetRepository : CoroutineCrudRepository<BudgetEntity, UUID> {
    suspend fun findByBudgetMonthAndBudgetYear(
        month: Int,
        year: Int,
    ): BudgetEntity?

    @Query(
        "SELECT * FROM budgets b WHERE " +
            "(b.budget_year > :startYear OR (b.budget_year = :startYear AND b.budget_month >= :startMonth)) AND " +
            "(b.budget_year < :endYear OR (b.budget_year = :endYear AND b.budget_month <= :endMonth))",
    )
    suspend fun findBudgetsInRange(
        startMonth: Int,
        endMonth: Int,
        startYear: Int,
        endYear: Int,
    ): Flow<BudgetEntity>
}

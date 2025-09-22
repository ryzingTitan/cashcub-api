package com.ryzingtitan.cashcub.data.budgets.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("budgets")
data class BudgetEntity(
    @Id
    val id: UUID? = null,
    val budgetMonth: Int,
    val budgetYear: Int,
)

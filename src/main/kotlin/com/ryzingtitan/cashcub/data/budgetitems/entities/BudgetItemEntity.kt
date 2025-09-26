package com.ryzingtitan.cashcub.data.budgetitems.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.UUID

@Table("budget_items")
data class BudgetItemEntity(
    @Id
    val id: UUID? = null,
    val name: String,
    val plannedAmount: BigDecimal,
    val budgetId: UUID,
    val categoryId: UUID,
)

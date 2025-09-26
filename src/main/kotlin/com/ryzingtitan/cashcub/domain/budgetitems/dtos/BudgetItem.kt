package com.ryzingtitan.cashcub.domain.budgetitems.dtos

import java.math.BigDecimal
import java.util.UUID

data class BudgetItem(
    val id: UUID,
    val name: String,
    val plannedAmount: BigDecimal,
    val budgetId: UUID,
    val categoryId: UUID,
)

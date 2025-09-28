package com.ryzingtitan.cashcub.domain.budgetitems.dtos

import java.math.BigDecimal
import java.util.UUID

data class BudgetItemRequest(
    val name: String,
    val plannedAmount: BigDecimal,
    val categoryId: UUID,
)

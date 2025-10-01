package com.ryzingtitan.cashcub.domain.budgets.dtos

import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import java.math.BigDecimal
import java.util.UUID

data class BudgetSummary(
    val id: UUID,
    val month: Int,
    val year: Int,
    val expectedIncome: BigDecimal,
    val actualIncome: BigDecimal,
    val expectedExpenses: BigDecimal,
    val actualExpenses: BigDecimal,
    val budgetItems: List<BudgetItem>,
)

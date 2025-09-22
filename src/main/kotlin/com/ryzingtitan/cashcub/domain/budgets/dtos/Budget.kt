package com.ryzingtitan.cashcub.domain.budgets.dtos

import java.util.UUID

data class Budget(
    val id: UUID,
    val month: Int,
    val year: Int,
)

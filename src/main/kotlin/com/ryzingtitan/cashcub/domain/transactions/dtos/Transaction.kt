package com.ryzingtitan.cashcub.domain.transactions.dtos

import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Transaction(
    val id: UUID,
    val date: Instant,
    val amount: BigDecimal,
    val transactionType: TransactionType,
    val merchant: String? = null,
    val notes: String? = null,
    val budgetId: UUID,
    val budgetItemId: UUID,
)

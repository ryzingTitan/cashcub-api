package com.ryzingtitan.cashcub.data.transactions.entities

import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("transactions")
data class TransactionEntity(
    @Id
    val id: UUID? = null,
    val date: Instant,
    val amount: BigDecimal,
    val transactionType: TransactionType,
    val merchant: String? = null,
    val notes: String? = null,
    val budgetId: UUID,
    val budgetItemId: UUID,
)

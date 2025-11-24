package com.ryzingtitan.cashcub.domain.transactions.mappers

import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import java.util.UUID

fun TransactionEntity.toDto() =
    Transaction(
        id = this.id!!,
        date = this.date,
        amount = this.amount.setScale(2),
        transactionType = this.transactionType,
        merchant = this.merchant,
        notes = this.notes,
        budgetId = this.budgetId,
        budgetItemId = this.budgetItemId,
    )

fun TransactionRequest.toEntity(
    budgetId: UUID,
    budgetItemId: UUID,
) = TransactionEntity(
    date = this.date,
    amount = this.amount,
    transactionType = this.transactionType,
    merchant = this.merchant,
    notes = this.notes,
    budgetId = budgetId,
    budgetItemId = budgetItemId,
)

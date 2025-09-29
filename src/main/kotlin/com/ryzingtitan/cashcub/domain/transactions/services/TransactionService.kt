package com.ryzingtitan.cashcub.domain.transactions.services

import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
) {
    suspend fun getAllByBudgetItemIdAndBudgetId(
        budgetItemId: UUID,
        budgetId: UUID,
    ): Flow<Transaction> {
        logger.info("Retrieving all transactions for budget item id $budgetItemId and budget id $budgetId")

        return transactionRepository.findAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId).map {
            Transaction(
                id = it.id!!,
                date = it.date,
                amount = it.amount.setScale(2),
                transactionType = it.transactionType,
                merchant = it.merchant,
                notes = it.notes,
                budgetId = it.budgetId,
                budgetItemId = it.budgetItemId,
            )
        }
    }

    suspend fun create(
        transactionRequest: TransactionRequest,
        budgetId: UUID,
        budgetItemId: UUID,
    ): Transaction {
        logger.info("Creating a transaction for budget item id $budgetItemId and budget id $budgetId")

        val createdTransaction =
            transactionRepository.save(
                TransactionEntity(
                    date = transactionRequest.date,
                    amount = transactionRequest.amount,
                    transactionType = transactionRequest.transactionType,
                    merchant = transactionRequest.merchant,
                    notes = transactionRequest.notes,
                    budgetId = budgetId,
                    budgetItemId = budgetItemId,
                ),
            )

        return Transaction(
            id = createdTransaction.id!!,
            date = createdTransaction.date,
            amount = createdTransaction.amount,
            transactionType = createdTransaction.transactionType,
            merchant = createdTransaction.merchant,
            notes = createdTransaction.notes,
            budgetId = createdTransaction.budgetId,
            budgetItemId = createdTransaction.budgetItemId,
        )
    }

    private val logger: Logger = LoggerFactory.getLogger(TransactionService::class.java)
}

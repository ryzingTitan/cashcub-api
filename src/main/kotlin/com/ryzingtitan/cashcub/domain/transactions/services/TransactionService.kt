package com.ryzingtitan.cashcub.domain.transactions.services

import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.exceptions.TransactionDoesNotExistException
import com.ryzingtitan.cashcub.domain.transactions.mappers.toDto
import com.ryzingtitan.cashcub.domain.transactions.mappers.toEntity
import com.ryzingtitan.cashcub.shared.getLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
) {
    private val log = getLogger<TransactionService>()

    suspend fun getAllByBudgetItemIdAndBudgetId(
        budgetItemId: UUID,
        budgetId: UUID,
    ): Flow<Transaction> {
        log.info("Retrieving all transactions for budget item id $budgetItemId and budget id $budgetId")
        return transactionRepository.findAllByBudgetItemIdAndBudgetId(budgetItemId, budgetId).map { it.toDto() }
    }

    suspend fun create(
        transactionRequest: TransactionRequest,
        budgetId: UUID,
        budgetItemId: UUID,
    ): Transaction {
        log.info("Creating a transaction for budget item id $budgetItemId and budget id $budgetId")
        require(transactionRequest.amount > BigDecimal.ZERO) { "Transaction amount must be positive" }

        val entity = transactionRequest.toEntity(budgetId, budgetItemId)
        return transactionRepository.save(entity).toDto()
    }

    suspend fun update(
        transactionId: UUID,
        budgetItemId: UUID,
        budgetId: UUID,
        transactionRequest: TransactionRequest,
    ): Transaction {
        log.info("Updating transaction with id $transactionId")
        require(transactionRequest.amount > BigDecimal.ZERO) { "Transaction amount must be positive" }

        transactionRepository.findById(transactionId) ?: run {
            log.error("Transaction does not exist for budget item id $budgetItemId and budget id $budgetId")
            throw TransactionDoesNotExistException("Transaction with id $transactionId not found")
        }

        val entity = transactionRequest.toEntity(budgetId, budgetItemId).copy(id = transactionId)
        return transactionRepository.save(entity).toDto()
    }

    suspend fun delete(
        transactionId: UUID,
        budgetItemId: UUID,
        budgetId: UUID,
    ) {
        log.info("Deleting transaction with id $transactionId from budget item id $budgetItemId and budget id $budgetId")
        transactionRepository.deleteById(transactionId)
    }
}

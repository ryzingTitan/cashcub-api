package com.ryzingtitan.cashcub.data.transactions.repositories

import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface TransactionRepository : CoroutineCrudRepository<TransactionEntity, UUID>

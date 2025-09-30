package com.ryzingtitan.cashcub.cucumber.data

import com.ryzingtitan.cashcub.data.transactions.entities.TransactionEntity
import com.ryzingtitan.cashcub.data.transactions.repositories.TransactionRepository
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.Instant
import java.util.UUID
import kotlin.collections.forEach
import kotlin.test.assertEquals

class TransactionRepositoryStepDefs(
    private val transactionRepository: TransactionRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following transactions exist:")
    fun theFollowingTransactionsExist(existingTransactions: List<TransactionEntity>) {
        existingTransactions.forEach { transaction ->
            r2dbcEntityTemplate.insert(transaction).block()
        }
    }

    @Then("the following transactions will exist:")
    fun theFollowingTransactionsWillExist(expectedTransactions: List<TransactionEntity>) {
        runBlocking {
            val actualTransactions = transactionRepository.findAll().toList()

            assertEquals(expectedTransactions.size, actualTransactions.size)

            expectedTransactions.forEachIndexed { index, expectedTransaction ->
                assertEquals(expectedTransaction.date, actualTransactions[index].date)
                assertEquals(expectedTransaction.amount, actualTransactions[index].amount)
                assertEquals(expectedTransaction.transactionType, actualTransactions[index].transactionType)
                assertEquals(expectedTransaction.merchant, actualTransactions[index].merchant)
                assertEquals(expectedTransaction.notes, actualTransactions[index].notes)
                assertEquals(expectedTransaction.budgetId, actualTransactions[index].budgetId)
                assertEquals(expectedTransaction.budgetItemId, actualTransactions[index].budgetItemId)
            }
        }
    }

    @DataTableType
    fun mapTransactionEntity(tableRow: Map<String, String>): TransactionEntity =
        TransactionEntity(
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            date = Instant.parse(tableRow["date"].toString()),
            amount = tableRow["amount"]!!.toBigDecimal(),
            transactionType = TransactionType.valueOf(tableRow["transactionType"]!!),
            merchant = tableRow["merchant"],
            notes = tableRow["notes"],
            budgetId = UUID.fromString(tableRow["budgetId"]),
            budgetItemId = UUID.fromString(tableRow["budgetItemId"]),
        )
}

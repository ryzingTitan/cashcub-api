package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.enums.TransactionType
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

class TransactionControllerStepDefs {
    @When("all transactions are retrieved for budget item {string} and  budget {string}")
    fun allTransactionsAreRetrievedForBudgetItemAndBudget(
        budgetItemId: String,
        budgetId: String,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/budgets/$budgetId/items/$budgetItemId/transactions")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val transactions = clientResponse.awaitEntityList<Transaction>().body

                        if (transactions != null) {
                            returnedTransactions.addAll(transactions)
                        }
                    }
                }
        }
    }

    @When("a transaction is created with the following data for budget {string} and budget item {string}:")
    fun aTransactionIsCreatedWithTheFollowingDataForBudgetAndBudgetItem(
        budgetId: String,
        budgetItemId: String,
        transactionRequests: List<TransactionRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/budgets/$budgetId/items/$budgetItemId/transactions")
                .bodyValue(transactionRequests.first())
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.CREATED) {
                        val transaction = clientResponse.awaitEntity<Transaction>().body

                        if (transaction != null) {
                            returnedTransactions.add(transaction)
                        }
                    }
                }
        }
    }

    @When("a transaction with id {string} is updated for budget item {string} and budget {string}:")
    fun aTransactionWithIdIsUpdatedForBudgetItemAndBudget(
        transactionId: String,
        budgetItemId: String,
        budgetId: String,
        transactionRequests: List<TransactionRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/budgets/$budgetId/items/$budgetItemId/transactions/$transactionId")
                .bodyValue(transactionRequests.first())
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val transaction = clientResponse.awaitEntity<Transaction>().body

                        if (transaction != null) {
                            returnedTransactions.add(transaction)
                        }
                    }
                }
        }
    }

    @Then("the following transactions are returned:")
    fun theFollowingBudgetItemsAreReturned(expectedTransaction: List<Transaction>) {
        assertEquals(expectedTransaction.size, returnedTransactions.size)

        expectedTransaction.forEachIndexed { index, expectedTransaction ->
            assertEquals(expectedTransaction.date, returnedTransactions[index].date)
            assertEquals(expectedTransaction.amount, returnedTransactions[index].amount)
            assertEquals(expectedTransaction.transactionType, returnedTransactions[index].transactionType)
            assertEquals(expectedTransaction.merchant, returnedTransactions[index].merchant)
            assertEquals(expectedTransaction.notes, returnedTransactions[index].notes)
            assertEquals(expectedTransaction.budgetId, returnedTransactions[index].budgetId)
            assertEquals(expectedTransaction.budgetItemId, returnedTransactions[index].budgetItemId)
        }
    }

    @DataTableType
    fun mapTransaction(tableRow: Map<String, String>): Transaction =
        Transaction(
            id = UUID.randomUUID(),
            date = Instant.parse(tableRow["date"].toString()),
            amount = tableRow["amount"]!!.toBigDecimal(),
            transactionType = TransactionType.valueOf(tableRow["transactionType"]!!),
            merchant = tableRow["merchant"],
            notes = tableRow["notes"],
            budgetId = UUID.fromString(tableRow["budgetId"]),
            budgetItemId = UUID.fromString(tableRow["budgetItemId"]),
        )

    @DataTableType
    fun mapTransactionRequest(tableRow: Map<String, String>): TransactionRequest =
        TransactionRequest(
            date = Instant.parse(tableRow["date"].toString()),
            amount = tableRow["amount"]!!.toBigDecimal(),
            transactionType = TransactionType.valueOf(tableRow["transactionType"]!!),
            merchant = tableRow["merchant"],
            notes = tableRow["notes"],
        )

    private val returnedTransactions = mutableListOf<Transaction>()
}

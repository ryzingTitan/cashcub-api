package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.test.assertEquals

class BudgetSummaryControllerStepDefs {
    @When("a budget summary is retrieved for budget id {string}")
    fun aBudgetSummaryIsRetrievedForBudgetId(budgetId: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/budgets/$budgetId")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val budgetSummary = clientResponse.awaitEntity<BudgetSummary>().body

                        if (budgetSummary != null) {
                            returnedBudgetSummaries.add(budgetSummary)
                        }
                    }
                }
        }
    }

    @Then("the following budget summaries are returned:")
    fun theFollowingBudgetSummariesAreReturned(expectedBudgetSummaries: List<BudgetSummary>) {
        assertEquals(expectedBudgetSummaries.size, returnedBudgetSummaries.size)

        expectedBudgetSummaries.forEachIndexed { index, expectedBudgetSummary ->
            assertEquals(expectedBudgetSummary.year, returnedBudgetSummaries[index].year)
            assertEquals(expectedBudgetSummary.month, returnedBudgetSummaries[index].month)
            assertEquals(expectedBudgetSummary.expectedIncome, returnedBudgetSummaries[index].expectedIncome)
            assertEquals(expectedBudgetSummary.actualIncome, returnedBudgetSummaries[index].actualIncome)
            assertEquals(expectedBudgetSummary.expectedExpenses, returnedBudgetSummaries[index].expectedExpenses)
            assertEquals(expectedBudgetSummary.actualExpenses, returnedBudgetSummaries[index].actualExpenses)
        }
    }

    @Then("the following budget items are returned in the summary for budget {string}:")
    fun theFollowingBudgetItemsAreReturnedInTheSummary(
        budgetId: String,
        expectedBudgetItems: List<BudgetItem>,
    ) {
        val returnedBudgetSummary =
            if (budgetId == "00000000-0000-0000-0000-000000000000") {
                returnedBudgetSummaries.first()
            } else {
                returnedBudgetSummaries.find { it.id == UUID.fromString(budgetId) }
            }

        assertEquals(expectedBudgetItems.size, returnedBudgetSummary?.budgetItems?.size)

        val returnedBudgetItems = returnedBudgetSummary!!.budgetItems
        expectedBudgetItems.forEachIndexed { index, expectedBudgetItem ->
            assertEquals(expectedBudgetItem.name, returnedBudgetItems[index].name)
            assertEquals(expectedBudgetItem.plannedAmount, returnedBudgetItems[index].plannedAmount)
            assertEquals(expectedBudgetItem.actualAmount, returnedBudgetItems[index].actualAmount)

            if (expectedBudgetItem.budgetId != UUID.fromString("00000000-0000-0000-0000-000000000000")) {
                assertEquals(expectedBudgetItem.budgetId, returnedBudgetItems[index].budgetId)
            }

            assertEquals(expectedBudgetItem.categoryId, returnedBudgetItems[index].categoryId)
        }
    }

    @DataTableType
    fun mapBudgetSummary(tableRow: Map<String, String>): BudgetSummary =
        BudgetSummary(
            id = UUID.randomUUID(),
            month = tableRow["month"]!!.toInt(),
            year = tableRow["year"]!!.toInt(),
            expectedIncome = tableRow["expectedIncome"]!!.toBigDecimal(),
            actualIncome = tableRow["actualIncome"]!!.toBigDecimal(),
            expectedExpenses = tableRow["expectedExpenses"]!!.toBigDecimal(),
            actualExpenses = tableRow["actualExpenses"]!!.toBigDecimal(),
            budgetItems = emptyList(),
        )

    companion object {
        val returnedBudgetSummaries = mutableListOf<BudgetSummary>()
    }
}

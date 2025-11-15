package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.cucumber.controllers.BudgetSummaryControllerStepDefs.Companion.returnedBudgetSummaries
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetRequest
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.test.assertEquals

class BudgetControllerStepDefs {
    @When("all budgets are retrieved")
    fun allBudgetsAreRetrieved() {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/budgets")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val budgets = clientResponse.awaitEntityList<Budget>().body

                        if (budgets != null) {
                            returnedBudgets.addAll(budgets)
                        }
                    }
                }
        }
    }

    @When("a budget is created for month {int} and year {int}")
    fun aBudgetIsCreatedForMonthAndYear(
        month: Int,
        year: Int,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/budgets")
                .bodyValue(BudgetRequest(month = month, year = year))
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.CREATED) {
                        val budget = clientResponse.awaitEntity<Budget>().body

                        if (budget != null) {
                            returnedBudgets.add(budget)
                        }
                    }
                }
        }
    }

    @When("a budget with id {string} is cloned for month {int} and year {int}")
    fun aBudgetWithIdIsCloneForMonthAndYear(
        budgetId: String,
        month: Int,
        year: Int,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/budgets/$budgetId/clone")
                .bodyValue(BudgetRequest(month = month, year = year))
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.CREATED) {
                        val budgetSummary = clientResponse.awaitEntity<BudgetSummary>().body

                        if (budgetSummary != null) {
                            returnedBudgetSummaries.add(budgetSummary)
                        }
                    }
                }
        }
    }

    @Then("the following budgets are returned:")
    fun theFollowingBudgetsAreReturned(expectedBudgets: List<Budget>) {
        assertEquals(expectedBudgets.size, returnedBudgets.size)

        expectedBudgets.forEachIndexed { index, expectedBudget ->
            assertEquals(expectedBudget.year, returnedBudgets[index].year)
            assertEquals(expectedBudget.month, returnedBudgets[index].month)
        }
    }

    @DataTableType
    fun mapBudget(tableRow: Map<String, String>): Budget =
        Budget(
            id = UUID.randomUUID(),
            month = tableRow["month"]!!.toInt(),
            year = tableRow["year"]!!.toInt(),
        )

    private val returnedBudgets = mutableListOf<Budget>()
}

package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.collections.forEach
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BudgetControllerStepDefs {
    @When("all budgets are retrieved")
    fun whenAllBudgetsAreRetrieved() {
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

    @Then("the following budgets are returned:")
    fun thenTheFollowingBudgetsAreReturned(expectedBudgets: List<Budget>) {
        assertEquals(expectedBudgets.size, returnedBudgets.size)

        expectedBudgets.forEach { expectedBudget ->
            assertTrue("$expectedBudget does not exist in $returnedBudgets") {
                returnedBudgets.any {
                    it.month == expectedBudget.month && it.year == expectedBudget.year
                }
            }
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

package com.ryzingtitan.cashcub.cucumber.data

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

class BudgetRepositoryStepDefs(
    private val budgetRepository: BudgetRepository,
) {
    @Given("the following budgets exist:")
    fun theFollowingBudgetsExist(existingBudgets: List<BudgetEntity>) {
        runBlocking {
            budgetRepository.saveAll(existingBudgets).toList()
        }
    }

    @Then("the following budgets will exist:")
    fun theFollowingBudgetsWillExist(expectedBudgets: List<BudgetEntity>) {
        runBlocking {
            val actualBudgets = budgetRepository.findAll().toList()

            expectedBudgets.forEachIndexed { index, expectedBudget ->
                assertEquals(expectedBudget.budgetYear, actualBudgets[index].budgetYear)
                assertEquals(expectedBudget.budgetMonth, actualBudgets[index].budgetMonth)
            }
        }
    }

    @DataTableType
    fun mapBudgetEntity(tableRow: Map<String, String>): BudgetEntity =
        BudgetEntity(
            budgetMonth = tableRow["budgetMonth"]!!.toInt(),
            budgetYear = tableRow["budgetYear"]!!.toInt(),
        )
}

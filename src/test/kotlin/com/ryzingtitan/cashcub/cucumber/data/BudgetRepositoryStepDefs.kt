package com.ryzingtitan.cashcub.cucumber.data

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import com.ryzingtitan.cashcub.data.budgets.repositories.BudgetRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.util.UUID
import kotlin.test.assertEquals

class BudgetRepositoryStepDefs(
    private val budgetRepository: BudgetRepository,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following budgets exist:")
    fun theFollowingBudgetsExist(existingBudgets: List<BudgetEntity>) {
        existingBudgets.forEach { budgetEntity ->
            r2dbcEntityTemplate.insert(budgetEntity).block()
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
            id = if (tableRow["id"].isNullOrEmpty()) null else UUID.fromString(tableRow["id"]),
            budgetMonth = tableRow["budgetMonth"]!!.toInt(),
            budgetYear = tableRow["budgetYear"]!!.toInt(),
        )
}

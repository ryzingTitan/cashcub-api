package com.ryzingtitan.cashcub.cucumber.data

import com.ryzingtitan.cashcub.data.budgets.entities.BudgetEntity
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.util.UUID

class BudgetRepositoryStepDefs(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    @Given("the following budgets exist:")
    fun theFollowingBudgets(budgets: List<BudgetEntity>) {
        budgets.forEach { r2dbcEntityTemplate.insert(it).block() }
    }

    @DataTableType
    fun mapBudgetEntity(tableRow: Map<String, String>): BudgetEntity =
        BudgetEntity(
            id = UUID.randomUUID(),
            budgetMonth = tableRow["month"]!!.toInt(),
            budgetYear = tableRow["year"]!!.toInt(),
        )
}

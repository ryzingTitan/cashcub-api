package com.ryzingtitan.cashcub.cucumber.data

import com.ryzingtitan.cashcub.data.budgetitems.entities.BudgetItemEntity
import com.ryzingtitan.cashcub.data.budgetitems.repositories.BudgetItemRepository
import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.assertEquals

class BudgetItemRepositoryStepDefs(
    private val budgetItemRepository: BudgetItemRepository,
    private val categoryRepository: CategoryRepository,
) {
    @Given("the following budget items exist:")
    fun theFollowingBudgetItemsExist(existingBudgetItems: List<BudgetItemEntity>) {
        runBlocking {
            budgetItemRepository.saveAll(existingBudgetItems).toList()
        }
    }

    @Then("the following budget items will exist:")
    fun theFollowingBudgetItemsWillExist(expectedBudgetItems: List<BudgetItemEntity>) {
        runBlocking {
            val actualBudgetItems = budgetItemRepository.findAll().toList()

            expectedBudgetItems.forEachIndexed { index, expectedBudgetItem ->
                assertEquals(expectedBudgetItem.name, actualBudgetItems[index].name)
                assertEquals(expectedBudgetItem.plannedAmount, actualBudgetItems[index].plannedAmount)
                assertEquals(expectedBudgetItem.budgetId, actualBudgetItems[index].budgetId)
                assertEquals(expectedBudgetItem.categoryId, actualBudgetItems[index].categoryId)
            }
        }
    }

    @DataTableType
    fun mapBudgetItemEntity(tableRow: Map<String, String>): BudgetItemEntity {
        lateinit var categoryId: UUID

        runBlocking {
            categoryId = categoryRepository.findByName(tableRow["categoryName"].toString())!!.id!!
        }

        return BudgetItemEntity(
            name = tableRow["name"].toString(),
            plannedAmount = tableRow["plannedAmount"]!!.toBigDecimal(),
            budgetId = UUID.fromString(tableRow["budgetId"]),
            categoryId = categoryId,
        )
    }
}

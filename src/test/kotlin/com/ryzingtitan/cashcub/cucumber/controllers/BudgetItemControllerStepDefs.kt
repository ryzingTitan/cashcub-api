package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItemRequest
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

class BudgetItemControllerStepDefs(
    private val categoryRepository: CategoryRepository,
) {
    @When("all budget items are retrieved for budget {string}")
    fun allBudgetItemsAreRetrievedForBudget(budgetId: String) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/budgets/$budgetId/items")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val budgetItems = clientResponse.awaitEntityList<BudgetItem>().body

                        if (budgetItems != null) {
                            returnedBudgetItems.addAll(budgetItems)
                        }
                    }
                }
        }
    }

    @When("a budget item is created with the following data for budget {string}:")
    fun aBudgetItemIsCreatedWithTheFollowingDataForBudget(
        budgetId: String,
        budgetItemRequests: List<BudgetItemRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .post()
                .uri("/budgets/$budgetId/items")
                .bodyValue(budgetItemRequests.first())
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.CREATED) {
                        val budgetItem = clientResponse.awaitEntity<BudgetItem>().body

                        if (budgetItem != null) {
                            returnedBudgetItems.add(budgetItem)
                        }
                    }
                }
        }
    }

    @When("a budget item with id {string} is updated with the following data for budget {string}:")
    fun aBudgetItemWithIdIsUpdatedWithTheFollowingDataForBudget(
        budgetItemId: String,
        budgetId: String,
        budgetItemRequests: List<BudgetItemRequest>,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .put()
                .uri("/budgets/$budgetId/items/$budgetItemId")
                .bodyValue(budgetItemRequests.first())
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val budgetItem = clientResponse.awaitEntity<BudgetItem>().body

                        if (budgetItem != null) {
                            returnedBudgetItems.add(budgetItem)
                        }
                    }
                }
        }
    }

    @When("a budget item with id {string} is deleted for budget {string}")
    fun aBudgetItemWithIdIsDeletedForBudget(
        budgetItemId: String,
        budgetId: String,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .delete()
                .uri("/budgets/$budgetId/items/$budgetItemId")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus
                }
        }
    }

    @Then("the following budget items are returned:")
    fun theFollowingBudgetItemsAreReturned(expectedBudgetItems: List<BudgetItem>) {
        expectedBudgetItems.forEachIndexed { index, expectedBudgetItem ->
            assertEquals(expectedBudgetItem.name, returnedBudgetItems[index].name)
            assertEquals(expectedBudgetItem.plannedAmount, returnedBudgetItems[index].plannedAmount)
            assertEquals(expectedBudgetItem.budgetId, returnedBudgetItems[index].budgetId)
            assertEquals(expectedBudgetItem.categoryId, returnedBudgetItems[index].categoryId)
        }
    }

    @DataTableType
    fun mapBudgetItem(tableRow: Map<String, String>): BudgetItem {
        lateinit var categoryId: UUID

        runBlocking {
            categoryId = categoryRepository.findByName(tableRow["categoryName"].toString())!!.id!!
        }

        return BudgetItem(
            id = UUID.randomUUID(),
            name = tableRow["name"].toString(),
            plannedAmount = tableRow["plannedAmount"]!!.toBigDecimal(),
            budgetId = UUID.fromString(tableRow["budgetId"]),
            categoryId = categoryId,
        )
    }

    @DataTableType
    fun mapCreateBudgetItemRequest(tableRow: Map<String, String>): BudgetItemRequest {
        lateinit var categoryId: UUID

        runBlocking {
            categoryId = categoryRepository.findByName(tableRow["categoryName"].toString())!!.id!!
        }

        return BudgetItemRequest(
            name = tableRow["name"].toString(),
            plannedAmount = tableRow["plannedAmount"]!!.toBigDecimal(),
            categoryId = categoryId,
        )
    }

    private val returnedBudgetItems = mutableListOf<BudgetItem>()
}

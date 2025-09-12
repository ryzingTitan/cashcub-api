package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.domain.categories.dtos.Category
import io.cucumber.java.DataTableType
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CategoryControllerStepDefs {
    @When("all categories are retrieved")
    fun whenAllCategoriesAreRetrieved() {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/categories")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val categories = clientResponse.awaitEntityList<Category>().body

                        if (categories != null) {
                            returnedCategories.addAll(categories)
                        }
                    }
                }
        }
    }

    @Then("the following categories are returned:")
    fun thenTheFollowingCategoriesAreReturned(expectedCategories: List<Category>) {
        assertEquals(expectedCategories.size, returnedCategories.size)

        expectedCategories.forEach { expectedCategory ->
            assertTrue("$expectedCategory does not exist in $returnedCategories") {
                returnedCategories.any {
                    it.name ==
                        expectedCategory.name
                }
            }
        }
    }

    @DataTableType
    fun mapCategory(tableRow: Map<String, String>): Category =
        Category(
            id = UUID.randomUUID(),
            name = tableRow["name"]!!,
        )

    private val returnedCategories = mutableListOf<Category>()
}

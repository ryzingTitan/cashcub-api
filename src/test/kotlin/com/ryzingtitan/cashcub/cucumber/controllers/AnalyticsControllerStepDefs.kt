package com.ryzingtitan.cashcub.cucumber.controllers

import com.ryzingtitan.cashcub.cucumber.common.CommonControllerStepDefs
import com.ryzingtitan.cashcub.cucumber.controllers.BudgetSummaryControllerStepDefs.Companion.returnedBudgetSummaries
import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.awaitEntityList
import org.springframework.web.reactive.function.client.awaitExchange

class AnalyticsControllerStepDefs {
    @When("analytics data is retrieved for date range from {string} to {string}")
    fun analyticsDataIsRetrievedForDateRange(
        startDate: String,
        endDate: String,
    ) {
        runBlocking {
            CommonControllerStepDefs.webClient
                .get()
                .uri("/analytics?startDate=$startDate&endDate=$endDate")
                .accept(MediaType.APPLICATION_JSON)
                .header(
                    "Authorization",
                    "Bearer ${CommonControllerStepDefs.authorizationToken?.serialize()}",
                ).awaitExchange { clientResponse ->
                    CommonControllerStepDefs.responseStatus = clientResponse.statusCode() as HttpStatus

                    if (clientResponse.statusCode() == HttpStatus.OK) {
                        val budgetSummary = clientResponse.awaitEntityList<BudgetSummary>().body

                        if (budgetSummary != null) {
                            returnedBudgetSummaries.addAll(budgetSummary)
                        }
                    }
                }
        }
    }
}

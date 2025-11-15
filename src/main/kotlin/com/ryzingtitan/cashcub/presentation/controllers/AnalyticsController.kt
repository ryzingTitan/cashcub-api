package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.BudgetSummary
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetSummaryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/analytics"])
class AnalyticsController(
    private val budgetService: BudgetService,
    private val budgetSummaryService: BudgetSummaryService,
) {
    @GetMapping
    @Tag(name = "Analytics")
    @Operation(summary = "Retrieve budget summaries for analytics")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of budget summaries",
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun getAnalyticsData(
        @RequestParam(required = true) startDate: String,
        @RequestParam(required = true) endDate: String,
    ): Flow<BudgetSummary> =
        budgetService
            .getAllForRange(startDate, endDate)
            .map { budget ->
                budgetSummaryService.getBudgetSummary(budget.id)
            }
}

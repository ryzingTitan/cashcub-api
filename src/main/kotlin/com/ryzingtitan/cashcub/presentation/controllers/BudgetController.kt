package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgets.dtos.Budget
import com.ryzingtitan.cashcub.domain.budgets.dtos.CreateBudgetRequest
import com.ryzingtitan.cashcub.domain.budgets.services.BudgetService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/budgets"])
class BudgetController(
    private val budgetService: BudgetService,
) {
    @GetMapping
    @Tag(name = "Budgets")
    @Operation(summary = "Retrieve all budgets")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of budgets",
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
    fun getBudgets(): Flow<Budget> = budgetService.getAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Tag(name = "Budgets")
    @Operation(summary = "Create a new budget")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "The budget that was created",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad Request - Ensure the request contains all required data",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Ensure the authorization token is valid",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict - Budget already exists",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun createBudget(
        @RequestBody createBudgetRequest: CreateBudgetRequest,
    ): Budget = budgetService.create(createBudgetRequest)
}

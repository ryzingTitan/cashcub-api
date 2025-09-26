package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.CreateBudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.services.BudgetItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/api/budgets/{budgetId}/items"])
class BudgetItemController(
    private val budgetItemService: BudgetItemService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Tag(name = "Budget Items")
    @Operation(summary = "Create a new budget item")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "The budget item that was created",
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
                description = "Conflict - Budget item already exists",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun createBudgetItem(
        @PathVariable("budgetId") budgetId: UUID,
        @RequestBody createBudgetItemRequest: CreateBudgetItemRequest,
    ): BudgetItem = budgetItemService.create(createBudgetItemRequest, budgetId)
}

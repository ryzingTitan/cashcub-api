package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItem
import com.ryzingtitan.cashcub.domain.budgetitems.dtos.BudgetItemRequest
import com.ryzingtitan.cashcub.domain.budgetitems.services.BudgetItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    @GetMapping
    @Tag(name = "Budget Items")
    @Operation(summary = "Retrieve all budget items for a budget")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of budget items for the budget",
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
    suspend fun getBudgetItemsByBudgetId(
        @PathVariable budgetId: UUID,
    ): Flow<BudgetItem> = budgetItemService.getAllByBudgetId(budgetId)

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
        @RequestBody budgetItemRequest: BudgetItemRequest,
    ): BudgetItem = budgetItemService.create(budgetItemRequest, budgetId)

    @PutMapping("/{budgetItemId}")
    @Tag(name = "Budget Items")
    @Operation(summary = "Update an existing budget item")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Budget item updated successfully",
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
                responseCode = "404",
                description = "Not Found - Budget item does not exist",
                content = arrayOf(Content()),
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun updateBudgetItem(
        @PathVariable(name = "budgetId") budgetId: UUID,
        @PathVariable(name = "budgetItemId") budgetItemId: UUID,
        @RequestBody budgetItemRequest: BudgetItemRequest,
    ): BudgetItem = budgetItemService.update(budgetItemId, budgetId, budgetItemRequest)

    @DeleteMapping("/{budgetItemId}")
    @Tag(name = "Budget Items")
    @Operation(summary = "Delete an existing budget item")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Budget Item deleted successfully",
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
                responseCode = "500",
                description = "Internal Server Error",
                content = arrayOf(Content()),
            ),
        ],
    )
    suspend fun deleteBudgetItem(
        @PathVariable(name = "budgetItemId") budgetItemId: UUID,
    ) {
        budgetItemService.delete(budgetItemId)
    }
}

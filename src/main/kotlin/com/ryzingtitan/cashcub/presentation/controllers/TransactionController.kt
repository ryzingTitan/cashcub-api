package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.transactions.dtos.Transaction
import com.ryzingtitan.cashcub.domain.transactions.dtos.TransactionRequest
import com.ryzingtitan.cashcub.domain.transactions.services.TransactionService
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
@RequestMapping(path = ["/api/budgets/{budgetId}/items/{budgetItemId}/transactions"])
class TransactionController(
    private val transactionService: TransactionService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Tag(name = "Transactions")
    @Operation(summary = "Create a new transaction")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "The transaction that was created",
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
    suspend fun createTransaction(
        @PathVariable("budgetId") budgetId: UUID,
        @PathVariable("budgetItemId") budgetItemId: UUID,
        @RequestBody transactionRequest: TransactionRequest,
    ): Transaction = transactionService.create(transactionRequest, budgetId, budgetItemId)
}

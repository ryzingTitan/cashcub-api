package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.categories.dtos.Category
import com.ryzingtitan.cashcub.domain.categories.services.CategoryService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/categories"])
class CategoryController(
    private val categoryService: CategoryService,
) {
    @GetMapping
    @Tag(name = "Categories")
    @Operation(summary = "Retrieve all categories")
    @SecurityRequirement(name = "jwt")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "The list of categories",
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
    fun getCategories(): Flow<Category> = categoryService.getAll()
}

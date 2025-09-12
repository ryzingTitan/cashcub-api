package com.ryzingtitan.cashcub.presentation.controllers

import com.ryzingtitan.cashcub.domain.categories.dtos.Category
import com.ryzingtitan.cashcub.domain.categories.services.CategoryService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import java.util.UUID

class CategoryControllerTests {
    @Nested
    inner class GetCategories {
        @Test
        fun `returns 'OK' status with all categories`() =
            runTest {
                whenever(mockCategoryService.getAll()).thenReturn(flowOf(firstCategory, secondCar))

                webTestClient
                    .get()
                    .uri("/api/categories")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBodyList<Category>()
                    .contains(firstCategory, secondCar)

                verify(mockCategoryService, times(1)).getAll()
            }
    }

    @BeforeEach
    fun setup() {
        val categoryController = CategoryController(mockCategoryService)
        webTestClient = WebTestClient.bindToController(categoryController).build()
    }

    private lateinit var webTestClient: WebTestClient

    private val mockCategoryService = mock<CategoryService>()

    private val firstCategory =
        Category(
            id = UUID.randomUUID(),
            name = "Category 1",
        )

    private val secondCar =
        Category(
            id = UUID.randomUUID(),
            name = "Category 2",
        )
}

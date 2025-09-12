package com.ryzingtitan.cashcub.domain.categories.services

import com.ryzingtitan.cashcub.data.categories.repositories.CategoryRepository
import com.ryzingtitan.cashcub.domain.categories.dtos.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    fun getAll(): Flow<Category> {
        logger.info("Retrieving all categories")

        return categoryRepository.findAll().map {
            Category(
                id = it.id!!,
                name = it.name,
            )
        }
    }

    private val logger: Logger = LoggerFactory.getLogger(CategoryService::class.java)
}

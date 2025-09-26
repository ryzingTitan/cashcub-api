package com.ryzingtitan.cashcub.data.categories.repositories

import com.ryzingtitan.cashcub.data.categories.entities.CategoryEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface CategoryRepository : CoroutineCrudRepository<CategoryEntity, UUID> {
    suspend fun findByName(name: String): CategoryEntity?
}

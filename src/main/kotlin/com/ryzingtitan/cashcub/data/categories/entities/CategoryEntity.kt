package com.ryzingtitan.cashcub.data.categories.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("categories")
data class CategoryEntity(
    @Id
    val id: UUID? = null,
    val name: String,
)

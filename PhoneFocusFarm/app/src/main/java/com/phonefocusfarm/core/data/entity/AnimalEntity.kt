package com.phonefocusfarm.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.phonefocusfarm.common.models.AnimalType
import com.phonefocusfarm.core.data.converter.AnimalTypeConverter

@Entity(tableName = "animals")
@TypeConverters(AnimalTypeConverter::class)
data class AnimalEntity(
    @PrimaryKey
    val id: String,
    
    val type: AnimalType,
    
    val posX: Float,
    val posY: Float,
    
    val velX: Float,
    val velY: Float,
    
    val state: String, // AnimalState
    
    val createdAt: Long,
    val updatedAt: Long
)
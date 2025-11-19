package com.phonefocusfarm.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val description: String,
    val icon: String,
    val condition: String,
    
    val progress: Int,
    val target: Int,
    
    val unlockedAt: Long?,
    val createdAt: Long
)
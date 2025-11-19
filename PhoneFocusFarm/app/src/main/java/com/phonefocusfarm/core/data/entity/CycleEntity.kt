package com.phonefocusfarm.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.phonefocusfarm.core.data.converter.CycleTypeConverter
import com.phonefocusfarm.common.models.CycleType

@Entity(tableName = "cycles")
@TypeConverters(CycleTypeConverter::class)
data class CycleEntity(
    @PrimaryKey
    val id: String,
    
    val startTime: Long,
    val endTime: Long?,
    val type: CycleType,
    
    val totalSessions: Int,
    val totalDuration: Long,
    
    val chickenCount: Int,
    val catCount: Int,
    val dogCount: Int,
    
    val achievements: List<String>,
    
    val createdAt: Long,
    val resetReason: String? = null // 重置原因
)
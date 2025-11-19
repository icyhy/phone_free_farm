package com.phonefocusfarm.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.phonefocusfarm.common.models.*
import com.phonefocusfarm.core.data.converter.FocusModeConverter
import com.phonefocusfarm.core.data.converter.IncubationResultConverter
import com.phonefocusfarm.core.data.converter.InterruptionReasonConverter
import com.phonefocusfarm.core.data.converter.AnimalTypeConverter

@Entity(tableName = "incubation_sessions")
@TypeConverters(
    FocusModeConverter::class,
    IncubationResultConverter::class,
    InterruptionReasonConverter::class,
    AnimalTypeConverter::class
)
data class IncubationSessionEntity(
    @PrimaryKey
    val id: String,
    
    val startTime: Long,
    val endTime: Long?,
    val duration: Long,
    
    val result: IncubationResult,
    val mode: FocusMode,
    val interruptionReason: InterruptionReason?,
    val animalGenerated: AnimalType?,
    
    val createdAt: Long
)
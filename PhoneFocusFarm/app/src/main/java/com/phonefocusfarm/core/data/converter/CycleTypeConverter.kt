package com.phonefocusfarm.core.data.converter

import androidx.room.TypeConverter
import com.phonefocusfarm.common.models.CycleType

class CycleTypeConverter {
    @TypeConverter
    fun fromCycleType(type: CycleType): String = type.name
    
    @TypeConverter
    fun toCycleType(type: String): CycleType = CycleType.valueOf(type)
}
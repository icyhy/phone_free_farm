package com.phonefocusfarm.core.data.converter

import androidx.room.TypeConverter
import com.phonefocusfarm.common.models.AnimalType

class AnimalTypeConverter {
    @TypeConverter
    fun fromAnimalType(type: AnimalType?): String? = type?.name
    
    @TypeConverter
    fun toAnimalType(type: String?): AnimalType? = type?.let { AnimalType.valueOf(it) }
}
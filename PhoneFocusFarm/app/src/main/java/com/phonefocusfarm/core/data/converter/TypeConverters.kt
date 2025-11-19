package com.phonefocusfarm.core.data.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString(",")
    
    @TypeConverter
    fun toStringList(data: String): List<String> = if (data.isEmpty()) emptyList() else data.split(",")
    
    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")
    
    @TypeConverter
    fun toIntList(data: String): List<Int> = if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}
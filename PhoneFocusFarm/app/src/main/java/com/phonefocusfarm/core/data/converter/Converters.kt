package com.phonefocusfarm.core.data.converter

import androidx.room.TypeConverter
import com.phonefocusfarm.common.models.FocusMode
import com.phonefocusfarm.common.models.IncubationResult
import com.phonefocusfarm.common.models.InterruptionReason

class FocusModeConverter {
    @TypeConverter
    fun fromFocusMode(mode: FocusMode): String = mode.name
    
    @TypeConverter
    fun toFocusMode(mode: String): FocusMode = FocusMode.valueOf(mode)
}

class IncubationResultConverter {
    @TypeConverter
    fun fromIncubationResult(result: IncubationResult): String = result.name
    
    @TypeConverter
    fun toIncubationResult(result: String): IncubationResult = IncubationResult.valueOf(result)
}

class InterruptionReasonConverter {
    @TypeConverter
    fun fromInterruptionReason(reason: InterruptionReason?): String? = reason?.name
    
    @TypeConverter
    fun toInterruptionReason(reason: String?): InterruptionReason? = reason?.let { InterruptionReason.valueOf(it) }
}
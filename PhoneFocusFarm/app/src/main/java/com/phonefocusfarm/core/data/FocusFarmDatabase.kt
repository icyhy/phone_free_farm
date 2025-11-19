package com.phonefocusfarm.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.phonefocusfarm.core.data.dao.AnimalDao
import com.phonefocusfarm.core.data.dao.IncubationSessionDao
import com.phonefocusfarm.core.data.dao.AchievementDao
import com.phonefocusfarm.core.data.dao.CycleDao
import com.phonefocusfarm.core.data.entity.AnimalEntity
import com.phonefocusfarm.core.data.entity.IncubationSessionEntity
import com.phonefocusfarm.core.data.entity.AchievementEntity
import com.phonefocusfarm.core.data.entity.CycleEntity
import com.phonefocusfarm.core.data.converter.Converters

@Database(
    entities = [
        AnimalEntity::class,
        IncubationSessionEntity::class,
        AchievementEntity::class,
        CycleEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusFarmDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun incubationSessionDao(): IncubationSessionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun cycleDao(): CycleDao
}
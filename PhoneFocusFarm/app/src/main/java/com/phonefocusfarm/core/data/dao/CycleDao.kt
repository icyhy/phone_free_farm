package com.phonefocusfarm.core.data.dao

import androidx.room.*
import com.phonefocusfarm.core.data.entity.CycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    
    @Query("SELECT * FROM cycles ORDER BY startTime DESC")
    fun getAllCycles(): Flow<List<CycleEntity>>
    
    @Query("SELECT * FROM cycles WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    fun getCurrentCycle(): Flow<CycleEntity?>
    
    @Query("SELECT * FROM cycles WHERE type = :type ORDER BY startTime DESC")
    fun getCyclesByType(type: String): Flow<List<CycleEntity>>
    
    @Query("SELECT * FROM cycles WHERE startTime >= :startTime AND endTime <= :endTime ORDER BY startTime DESC")
    fun getCyclesInRange(startTime: Long, endTime: Long): Flow<List<CycleEntity>>
    
    @Query("SELECT SUM(totalDuration) FROM cycles WHERE endTime IS NOT NULL")
    fun getTotalFocusTime(): Flow<Long?>
    
    @Query("SELECT SUM(totalSessions) FROM cycles WHERE endTime IS NOT NULL")
    fun getTotalSessions(): Flow<Int?>
    
    @Query("SELECT SUM(chickenCount) FROM cycles WHERE endTime IS NOT NULL")
    fun getTotalChickens(): Flow<Int?>
    
    @Query("SELECT SUM(catCount) FROM cycles WHERE endTime IS NOT NULL")
    fun getTotalCats(): Flow<Int?>
    
    @Query("SELECT SUM(dogCount) FROM cycles WHERE endTime IS NOT NULL")
    fun getTotalDogs(): Flow<Int?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCycle(cycle: CycleEntity)
    
    @Update
    suspend fun updateCycle(cycle: CycleEntity)
    
    @Query("UPDATE cycles SET endTime = :endTime, totalSessions = :totalSessions, totalDuration = :totalDuration, chickenCount = :chickenCount, catCount = :catCount, dogCount = :dogCount, achievements = :achievements WHERE id = :cycleId")
    suspend fun completeCycle(
        cycleId: String,
        endTime: Long,
        totalSessions: Int,
        totalDuration: Long,
        chickenCount: Int,
        catCount: Int,
        dogCount: Int,
        achievements: List<String>
    )
    
    @Query("DELETE FROM cycles WHERE id = :cycleId")
    suspend fun deleteCycle(cycleId: String)
    
    @Query("DELETE FROM cycles")
    suspend fun deleteAllCycles()
}
package com.phonefocusfarm.core.data.dao

import androidx.room.*
import com.phonefocusfarm.core.data.entity.IncubationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncubationSessionDao {
    
    @Query("SELECT * FROM incubation_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 100): Flow<List<IncubationSessionEntity>>
    
    @Query("SELECT * FROM incubation_sessions WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<IncubationSessionEntity>>
    
    @Query("SELECT * FROM incubation_sessions WHERE result = 'SUCCESS' ORDER BY startTime DESC")
    fun getSuccessfulSessions(): Flow<List<IncubationSessionEntity>>
    
    @Query("SELECT * FROM incubation_sessions WHERE result = 'INTERRUPTED' ORDER BY startTime DESC")
    fun getInterruptedSessions(): Flow<List<IncubationSessionEntity>>
    
    @Query("SELECT COUNT(*) FROM incubation_sessions WHERE result = 'SUCCESS'")
    fun getSuccessfulSessionCount(): Flow<Int>
    
    @Query("SELECT SUM(duration) FROM incubation_sessions WHERE result = 'SUCCESS'")
    fun getTotalFocusTime(): Flow<Long?>
    
    @Query("SELECT AVG(duration) FROM incubation_sessions WHERE result = 'SUCCESS'")
    fun getAverageFocusTime(): Flow<Long?>
    
    @Query("SELECT MAX(duration) FROM incubation_sessions WHERE result = 'SUCCESS'")
    fun getLongestFocusTime(): Flow<Long?>
    
    @Query("SELECT COUNT(*) FROM incubation_sessions WHERE animalGenerated = :animalType")
    fun getAnimalGeneratedCount(animalType: String): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: IncubationSessionEntity)
    
    @Update
    suspend fun updateSession(session: IncubationSessionEntity)
    
    @Query("DELETE FROM incubation_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)
    
    @Query("DELETE FROM incubation_sessions")
    suspend fun deleteAllSessions()
}
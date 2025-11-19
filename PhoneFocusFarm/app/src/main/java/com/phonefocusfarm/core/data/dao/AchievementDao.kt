package com.phonefocusfarm.core.data.dao

import androidx.room.*
import com.phonefocusfarm.core.data.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements ORDER BY createdAt DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT * FROM achievements WHERE unlockedAt IS NULL ORDER BY createdAt DESC")
    fun getLockedAchievements(): Flow<List<AchievementEntity>>
    
    @Query("SELECT COUNT(*) FROM achievements WHERE unlockedAt IS NOT NULL")
    fun getUnlockedAchievementCount(): Flow<Int>
    
    @Query("SELECT * FROM achievements WHERE id = :achievementId")
    fun getAchievementById(achievementId: String): Flow<AchievementEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)
    
    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)
    
    @Query("UPDATE achievements SET progress = :progress WHERE id = :achievementId")
    suspend fun updateAchievementProgress(achievementId: String, progress: Int)
    
    @Query("UPDATE achievements SET unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: Long)
    
    @Query("DELETE FROM achievements WHERE id = :achievementId")
    suspend fun deleteAchievement(achievementId: String)
    
    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}
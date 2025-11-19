package com.phonefocusfarm.core.data.dao

import androidx.room.*
import com.phonefocusfarm.core.data.entity.AnimalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    
    @Query("SELECT * FROM animals WHERE state = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveAnimals(): Flow<List<AnimalEntity>>
    
    @Query("SELECT * FROM animals ORDER BY createdAt DESC")
    fun getAllAnimals(): Flow<List<AnimalEntity>>
    
    @Query("SELECT * FROM animals WHERE type = :type AND state = 'ACTIVE'")
    fun getAnimalsByType(type: String): Flow<List<AnimalEntity>>
    
    @Query("SELECT COUNT(*) FROM animals WHERE state = 'ACTIVE'")
    fun getActiveAnimalCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM animals WHERE type = :type AND state = 'ACTIVE'")
    fun getAnimalCountByType(type: String): Flow<Int>

    @Query("SELECT * FROM animals WHERE type = :type AND state = 'ACTIVE' ORDER BY createdAt ASC LIMIT 1")
    suspend fun getOneActiveAnimalByType(type: String): AnimalEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimal(animal: AnimalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimals(animals: List<AnimalEntity>)
    
    @Update
    suspend fun updateAnimal(animal: AnimalEntity)
    
    @Update
    suspend fun updateAnimals(animals: List<AnimalEntity>)
    
    @Query("UPDATE animals SET state = 'INACTIVE' WHERE id = :animalId")
    suspend fun deactivateAnimal(animalId: String)
    
    @Query("DELETE FROM animals WHERE id = :animalId")
    suspend fun deleteAnimal(animalId: String)
    
    @Query("DELETE FROM animals WHERE state = 'INACTIVE'")
    suspend fun deleteInactiveAnimals()
    
    @Query("DELETE FROM animals")
    suspend fun deleteAllAnimals()
}
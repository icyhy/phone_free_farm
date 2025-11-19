package com.phonefocusfarm.ui.screens.farm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.common.models.Animal
import com.phonefocusfarm.common.models.AnimalType
import com.phonefocusfarm.common.models.Position
import com.phonefocusfarm.common.models.Velocity
import dagger.hilt.android.lifecycle.HiltViewModel
import android.graphics.Bitmap
import android.graphics.Rect
import com.phonefocusfarm.core.data.dao.AnimalDao
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmViewModel @Inject constructor(
    private val animalDao: AnimalDao
) : ViewModel() {
    
    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals.asStateFlow()
    
    private val _animalCount = MutableStateFlow<Map<AnimalType, Int>>(emptyMap())
    val animalCount: StateFlow<Map<AnimalType, Int>> = _animalCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _farmAreaBounds = MutableStateFlow<Rect?>(null)
    val farmAreaBounds: StateFlow<Rect?> = _farmAreaBounds.asStateFlow()
    
    private val _latestSnapshot = MutableStateFlow<Bitmap?>(null)
    val latestSnapshot: StateFlow<Bitmap?> = _latestSnapshot.asStateFlow()
    
    init {
        viewModelScope.launch {
            _isLoading.value = true
            animalDao.getActiveAnimals().collect { entities ->
                val mapped = entities.map { e ->
                    Animal(
                        id = e.id,
                        type = e.type,
                        position = Position(e.posX, e.posY),
                        velocity = Velocity(e.velX, e.velY)
                    )
                }
                _animals.value = mapped
                _animalCount.value = mapped.groupingBy { it.type }.eachCount()
                _isLoading.value = false
            }
        }
    }
    
    fun onAnimalClick(animal: Animal) {
        viewModelScope.launch {
            // TODO: 处理动物点击事件
            // 播放音效
            // 显示动画
            // 更新动物状态
        }
    }
    
    fun updateFarmAreaBounds(rect: Rect) {
        _farmAreaBounds.value = rect
    }
    
    fun updateFarmSnapshot(bitmap: Bitmap) {
        _latestSnapshot.value = bitmap
    }
}
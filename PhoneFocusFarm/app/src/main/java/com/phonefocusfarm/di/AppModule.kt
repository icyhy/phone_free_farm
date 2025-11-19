package com.phonefocusfarm.di

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import com.phonefocusfarm.core.data.FocusFarmDatabase
import com.phonefocusfarm.core.data.dao.AnimalDao
import com.phonefocusfarm.core.data.dao.CycleDao
import com.phonefocusfarm.core.data.dao.IncubationSessionDao
import com.phonefocusfarm.core.timer.TimerManager
import com.phonefocusfarm.core.timer.TimerManagerImpl
import com.phonefocusfarm.core.permission.PermissionManager
import com.phonefocusfarm.core.permission.HuaweiPermissionHelper
import com.phonefocusfarm.core.detector.InterruptionDetector
import com.phonefocusfarm.core.detector.InterruptionDetectorImpl
import com.phonefocusfarm.core.detector.UsageStatsDetector
import com.phonefocusfarm.common.constants.AppConstants
// import com.phonefocus.farm3d.Farm3DRenderer
// import com.phonefocus.farm3d.ai.AnimalGroupManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusFarmDatabase {
        return Room.databaseBuilder(
            context,
            FocusFarmDatabase::class.java,
            AppConstants.DATABASE_NAME
        ).fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideTimerManager(
        @ApplicationContext context: Context,
        interruptionDetector: InterruptionDetector,
        usageStatsDetector: UsageStatsDetector,
        animalDao: AnimalDao,
        incubationSessionDao: IncubationSessionDao
    ): TimerManager {
        return TimerManagerImpl(context, interruptionDetector, usageStatsDetector, animalDao, incubationSessionDao)
    }

    @Provides
    @Singleton
    fun provideAnimalDao(db: FocusFarmDatabase): AnimalDao {
        return db.animalDao()
    }

    @Provides
    @Singleton
    fun provideIncubationSessionDao(db: FocusFarmDatabase): IncubationSessionDao {
        return db.incubationSessionDao()
    }
    
    @Provides
    @Singleton
    fun provideCycleDao(db: FocusFarmDatabase): CycleDao {
        return db.cycleDao()
    }
    
    @Provides
    @Singleton
    fun provideHuaweiPermissionHelper(@ApplicationContext context: Context): HuaweiPermissionHelper {
        return HuaweiPermissionHelper(context)
    }
    
    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context,
        huaweiPermissionHelper: HuaweiPermissionHelper
    ): PermissionManager {
        return PermissionManager(context, huaweiPermissionHelper)
    }
    
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    @Provides
    @Singleton
    fun provideInterruptionDetector(@ApplicationContext context: Context): InterruptionDetector {
        return InterruptionDetectorImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideUsageStatsDetector(@ApplicationContext context: Context): UsageStatsDetector {
        return UsageStatsDetector(context)
    }

}
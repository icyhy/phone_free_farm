package com.phonefocusfarm.core.detector

import android.app.Activity
import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import com.phonefocusfarm.common.models.InterruptionReason
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

interface InterruptionDetector {
    fun startMonitoring()
    fun stopMonitoring()
    val interruptionEvents: Flow<InterruptionReason>
}

@Singleton
class InterruptionDetectorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : InterruptionDetector, Application.ActivityLifecycleCallbacks, SensorEventListener, DefaultLifecycleObserver, ComponentCallbacks2 {

    private val _interruptionEvents = MutableSharedFlow<InterruptionReason>()
    override val interruptionEvents: Flow<InterruptionReason> = _interruptionEvents.asSharedFlow()

    private var isMonitoring = false
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var lastAcceleration: FloatArray? = null
    private var lastGyroscope: FloatArray? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastTouchTime: Long = 0
    private var lastMovementTime: Long = 0
    private val movementThreshold = 1.5f
    private val touchCooldown = 1000L
    private val movementCooldown = 2000L

    private var currentActivity: Activity? = null
    private var touchListener: View.OnTouchListener? = null
    private var originalWindowCallback: Window.Callback? = null

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    override fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(this)
        (context.applicationContext as? Application)?.registerComponentCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        startTouchMonitoring()
        startAppStateMonitoring()
        startSensorMonitoring()

        handler.post {
            currentActivity?.let { setupTouchListener(it) }
        }
    }

    override fun stopMonitoring() {
        isMonitoring = false
        (context.applicationContext as? Application)?.unregisterActivityLifecycleCallbacks(this)
        (context.applicationContext as? Application)?.unregisterComponentCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        sensorManager?.unregisterListener(this)
        removeTouchListener()
    }

    private fun startTouchMonitoring() { }
    private fun startAppStateMonitoring() { }

    private fun startSensorMonitoring() {
        accelerometer?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun setupTouchListener(activity: Activity) {
        if (touchListener != null) return
        touchListener = View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTouchTime > touchCooldown) {
                        lastTouchTime = currentTime
                        notifyInterruption(InterruptionReason.TOUCH_EVENT)
                    }
                }
            }
            false
        }
        activity.window.decorView.setOnTouchListener(touchListener!!)

        // 全局触摸拦截：不影响分发，仅记录触摸
        originalWindowCallback = activity.window.callback
        val original = originalWindowCallback
        if (original != null) {
            activity.window.callback = object : Window.Callback by original {
                override fun dispatchTouchEvent(event: MotionEvent): Boolean {
                    val now = System.currentTimeMillis()
                    if (now - lastTouchTime > touchCooldown) {
                        lastTouchTime = now
                        notifyInterruption(InterruptionReason.TOUCH_EVENT)
                    }
                    return original.dispatchTouchEvent(event)
                }
            }
        }
    }

    private fun removeTouchListener() {
        touchListener = null
        currentActivity?.window?.decorView?.setOnTouchListener(null)
        currentActivity?.let { activity ->
            originalWindowCallback?.let { oc ->
                activity.window.callback = oc
            }
        }
        originalWindowCallback = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!isMonitoring) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val currentAcceleration = event.values.clone()
                lastAcceleration?.let { last ->
                    val deltaX = kotlin.math.abs(currentAcceleration[0] - last[0])
                    val deltaY = kotlin.math.abs(currentAcceleration[1] - last[1])
                    val deltaZ = kotlin.math.abs(currentAcceleration[2] - last[2])
                    val totalMovement = deltaX + deltaY + deltaZ
                    if (totalMovement > movementThreshold) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastMovementTime > movementCooldown) {
                            lastMovementTime = currentTime
                            notifyInterruption(InterruptionReason.DEVICE_MOVEMENT)
                        }
                    }
                }
                lastAcceleration = currentAcceleration
            }
            Sensor.TYPE_GYROSCOPE -> {
                val currentGyroscope = event.values.clone()
                lastGyroscope?.let { last ->
                    val deltaX = kotlin.math.abs(currentGyroscope[0] - last[0])
                    val deltaY = kotlin.math.abs(currentGyroscope[1] - last[1])
                    val deltaZ = kotlin.math.abs(currentGyroscope[2] - last[2])
                    val totalRotation = deltaX + deltaY + deltaZ
                    if (totalRotation > movementThreshold) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastMovementTime > movementCooldown) {
                            lastMovementTime = currentTime
                            notifyInterruption(InterruptionReason.DEVICE_MOVEMENT)
                        }
                    }
                }
                lastGyroscope = currentGyroscope
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    private var activityCount = 0
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) { activityCount++ }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
        setupTouchListener(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
            removeTouchListener()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount <= 0 && !activity.isChangingConfigurations) {
            notifyInterruption(InterruptionReason.APP_BACKGROUND)
        }
    }

    // 移除ProcessLifecycle的直接中断，改由activityCount判断，避免应用内导航误判
    override fun onStop(owner: LifecycleOwner) {
        notifyInterruption(InterruptionReason.APP_BACKGROUND)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityDestroyed(activity: Activity) { }

    private fun notifyInterruption(reason: InterruptionReason) {
        if (isMonitoring) {
            Log.d("InterruptionDetector", "notifyInterruption: $reason")
            _interruptionEvents.tryEmit(reason)
        }
    }

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            notifyInterruption(InterruptionReason.APP_BACKGROUND)
        }
    }

    override fun onLowMemory() { }

    override fun onConfigurationChanged(newConfig: Configuration) { }
}
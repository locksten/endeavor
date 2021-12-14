package com.example.endeavor.ui.battle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.endeavor.ui.RingWithBackground
import com.example.endeavor.ui.theme.Theme
import kotlin.math.*
import kotlin.random.Random

@ExperimentalComposeUiApi
@Composable
fun SpecialAttackMinigame(targetEmoji: String, onDone: (Float) -> Unit) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .background(Theme.colors.background)
                .fillMaxSize()
                .border(3.dp, Theme.colors.energy)
        ) {
            val duration = 6000
            val cursorSize = 32f
            val targetSizeRange = Pair(48f, 80f)
            val distanceThreshold = 0.05f
            var hitCount by remember { mutableStateOf(0) }
            val damageMultiplier = (1.0 + hitCount * 0.5).toFloat()
            val timer by animateIntAsState(
                targetValue = if (hitCount == 0) {
                    duration
                } else {
                    0
                },
                animationSpec = if (hitCount == 0) {
                    snap()
                } else {
                    tween(durationMillis = duration, easing = LinearEasing)
                },
            )

            if (timer == 0) {
                EndScreen(damageMultiplier, onDone)
            } else {
                val cursor = cursorPosition()
                var target by remember { mutableStateOf(randomPoint()) }
                var isHit by remember { mutableStateOf(false) }
                if (distance(cursor, target) < distanceThreshold) isHit = true
                val targetSize by animateFloatAsState(
                    targetValue = if (isHit) {
                        targetSizeRange.second
                    } else {
                        targetSizeRange.first
                    },
                    animationSpec = spring(
                        stiffness = Spring.StiffnessMedium,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    ),
                    finishedListener = {
                        if (isHit) {
                            target = randomPoint()
                            hitCount++
                            isHit = false
                        }
                    }
                )
                Minigame(
                    damageMultiplier = damageMultiplier,
                    targetSize = targetSize,
                    targetSizeRange = targetSizeRange,
                    cursorSize = cursorSize,
                    timer = timer,
                    duration = duration,
                    cursor = cursor,
                    target = target,
                    targetEmoji = targetEmoji
                )
            }
        }
    }
}

@Composable
private fun EndScreen(damageMultiplier: Float, onDone: (Float) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Damage Multiplier",
            fontSize = 25.sp,
        )
        Text(
            text = "${damageMultiplier}x",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onDone(damageMultiplier) }, contentPadding = PaddingValues(16.dp)) {
            Text(text = "Special Attack!", fontSize = 25.sp)
        }
    }
}

@Composable
private fun Minigame(
    damageMultiplier: Float,
    targetSize: Float,
    targetSizeRange: Pair<Float, Float>,
    cursorSize: Float,
    timer: Int,
    duration: Int,
    cursor: Pair<Float, Float>,
    target: Pair<Float, Float>,
    targetEmoji: String,
) {
    Box(contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Damage Multiplier",
                    fontSize = 20.sp
                )
                Text(
                    text = "${damageMultiplier}x",
                    fontSize = targetSize.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(96.dp)
            ) {
                Text(
                    text = "${(timer / 100f).roundToInt() / 10f}s",
                    fontSize = 20.sp
                )
                RingWithBackground(
                    color = Theme.colors.energy,
                    backgroundColor = Theme.colors.faintEnergy,
                    value = timer.toFloat() / duration.toFloat(),
                    thickness = 24f,
                    full = true
                )
            }
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "⚔️",
                fontSize = cursorSize.sp,
                modifier = Modifier
                    .offset(
                        x = maxWidth.times(cursor.first),
                        y = maxHeight.times(cursor.second)
                    )
                    .padding(
                        top = ((targetSizeRange.first - cursorSize) / 2f).dp,
                        start = ((targetSizeRange.first - cursorSize) / 2f).dp
                    )
            )
            Text(
                text = if (targetSize > targetSizeRange.second - 1f) {
                    "❌"
                } else {
                    targetEmoji
                },
                fontSize = targetSize.sp,
                modifier = Modifier.offset(
                    x = maxWidth.times(target.first),
                    y = maxHeight.times(target.second)
                )
            )
        }
    }
}

@Composable
private fun cursorPosition(): Pair<Float, Float> {
    val sensorManager =
        LocalContext.current.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val xs by remember { mutableStateOf(Array(5) { 0f }) }
    val ys by remember { mutableStateOf(Array(5) { 0f }) }
    var idx by remember { mutableStateOf(0) }
    var xRunningAverage by remember { mutableStateOf(0f) }
    var yRunningAverage by remember { mutableStateOf(0f) }
    val x by animateFloatAsState(
        targetValue = xRunningAverage,
        animationSpec = tween(
            durationMillis = 50,
            easing = LinearEasing
        )
    )
    val y by animateFloatAsState(
        targetValue = yRunningAverage,
        animationSpec = tween(
            durationMillis = 50,
            easing = LinearEasing
        )
    )
    val cursorPos = Pair(x, y)

    class Listener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val xSensor = event?.values?.get(0) ?: 0f
            val ySensor = event?.values?.get(1) ?: 0f
            xs[idx] = (clampTo0to1((-xSensor + 4) / 8f))
            ys[idx] = (clampTo0to1((ySensor + 1) / 8f))
            idx++
            idx %= xs.size

            xRunningAverage = xs.average().toFloat()
            yRunningAverage = ys.average().toFloat()
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    DisposableEffect(true) {
        val listener = Listener()
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return cursorPos
}

fun distance(a: Pair<Float, Float>, b: Pair<Float, Float>): Float {
    return sqrt((a.first - b.first).pow(2f) + (a.second - b.second).pow(2f))
}

fun randomPoint(): Pair<Float, Float> {
    return Pair(Random.nextDouble(0.2, 0.8).toFloat(), Random.nextDouble(0.2, 0.8).toFloat())
}

fun clampTo0to1(value: Float): Float {
    return clamp(value, 0f, 1f)
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return max(min, min(max, value))
}
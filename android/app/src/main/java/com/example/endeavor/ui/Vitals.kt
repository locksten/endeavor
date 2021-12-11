package com.example.endeavor.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.VitalsQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.Theme

@Composable
fun CVitals() {
    val vitals = gqlWatchQuery(VitalsQuery())?.me?.user
    vitals?.let {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "lvl ${vitals.level} ${vitals.username}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 32.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                HitpointRing(value = vitals.hitpoints, maxValue = vitals.maxHitpoints)
                EnergyRing(value = vitals.energy, maxValue = vitals.maxEnergy)
                ExperienceRing(
                    value = vitals.experienceInCurrentLevel,
                    maxValue = vitals.experienceForNexLevel
                )
            }
        }
    }

}

@Composable
private fun HitpointRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = "Health",
        color = Theme.colors.hitpoints,
        backgroundColor = Theme.colors.faintHitpoints,
        value = value,
        maxValue = maxValue,
        width = 70f,
        thickness = 40f,
        full = false
    )
}

@Composable
private fun EnergyRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = "Energy",
        color = Theme.colors.energy,
        backgroundColor = Theme.colors.faintEnergy,
        value = value,
        maxValue = maxValue,
        width = 70f,
        thickness = 40f,
        full = false
    )
}

@Composable
private fun ExperienceRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = "XP",
        countLabel = "${value ?: 0}",
        color = Theme.colors.experience,
        backgroundColor = Theme.colors.faintExperience,
        value = value,
        maxValue = maxValue,
        width = 70f,
        thickness = 40f,
        full = false
    )
}


@Composable
fun VitalRing(
    color: Color,
    backgroundColor: Color,
    value: Int?,
    maxValue: Int?,
    label: String?,
    countLabel: String? = "${value ?: 0} / ${maxValue ?: 0}",
    width: Float? = null,
    thickness: Float,
    full: Boolean
) {
    Box(
        modifier = if (width == null) {
            Modifier.aspectRatio(
                if (full) {
                    1f
                } else {
                    1.55f
                }
            )
        } else {
            Modifier.size((width * 1.5f).dp, (width * 1.2f).dp)
        }
    ) {
        AnimatedRing(
            color = color,
            backgroundColor = backgroundColor,
            targetValue = if (value == null || maxValue == null) {
                null
            } else {
                value.toFloat() / maxValue.toFloat()
            },
            thickness = thickness,
            full = full
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            countLabel?.let { Text(it) }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            label?.let { Text(it) }
        }
    }
}


@Composable
private fun AnimatedRing(
    color: Color,
    backgroundColor: Color,
    targetValue: Float?,
    thickness: Float,
    full: Boolean
) {
    var lastFinish by rememberSaveable { mutableStateOf(0f) }
    val animateFloat = remember { Animatable(lastFinish) }
    LaunchedEffect(animateFloat, targetValue) {
        lastFinish = animateFloat.animateTo(
            targetValue = targetValue ?: lastFinish,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        ).endState.value
    }
    RingWithBackground(
        color = color,
        backgroundColor = backgroundColor,
        value = animateFloat.value,
        thickness = thickness,
        full = full
    )
}

@Composable
private fun RingWithBackground(
    color: Color,
    backgroundColor: Color,
    value: Float,
    thickness: Float,
    full: Boolean
) {
    Box {
        Ring(color = backgroundColor, value = 1f, thickness = thickness, full = full)
        Ring(color = color, value = value, thickness = thickness, full = full)
    }
}

@Composable
private fun Ring(color: Color, value: Float, thickness: Float, full: Boolean) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawArc(
            color = color,
            startAngle = 165f,
            sweepAngle = (if (full) {
                360f
            } else {
                210f
            }) * value,
            useCenter = false,
            size = Size(
                this.size.width - thickness,
                this.size.width - thickness
            ),
            topLeft = Offset(thickness / 2f, thickness / 2f),
            style = Stroke(width = thickness, cap = StrokeCap.Round)
        )
    }
}
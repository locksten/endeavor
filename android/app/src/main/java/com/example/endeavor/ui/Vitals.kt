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
import androidx.compose.ui.unit.dp
import com.example.endeavor.VitalsQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.Theme

@Composable
fun Vitals() {
    val vitals = gqlWatchQuery(VitalsQuery())?.me?.user
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        HitpointRing(value = vitals?.hitpoints, maxValue = vitals?.maxHitpoints)
        EnergyRing(value = vitals?.energy, maxValue = vitals?.maxEnergy)
        ExperienceRing(value = vitals?.experience, maxValue = 1000)
    }
}

@Composable
private fun HitpointRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = "Health",
        color = Theme.colors.hitpoints,
        backgroundColor = Theme.colors.faintHitpoints,
        value = value,
        maxValue = maxValue
    )
}

@Composable
private fun EnergyRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = "Energy",
        color = Theme.colors.energy,
        backgroundColor = Theme.colors.faintEnergy,
        value = value,
        maxValue = maxValue
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
        maxValue = maxValue
    )
}


@Composable
private fun VitalRing(
    color: Color,
    backgroundColor: Color,
    value: Int?,
    maxValue: Int?,
    label: String,
    countLabel: String? = "${value ?: 0} / ${maxValue ?: 0}",
) {
    val width = 70f
    Box(Modifier.size((width * 1.5f).dp, (width * 1.2f).dp)) {
        AnimatedRing(
            color = color,
            backgroundColor = backgroundColor,
            targetValue = if (value == null || maxValue == null) {
                null
            } else {
                value.toFloat() / maxValue.toFloat()
            }
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
            Text(text = label)
        }
    }
}


@Composable
private fun AnimatedRing(color: Color, backgroundColor: Color, targetValue: Float?) {
    var lastFinish by rememberSaveable { mutableStateOf(0f) }
    val animateFloat = remember { Animatable(lastFinish) }
    LaunchedEffect(animateFloat, targetValue) {
        lastFinish = animateFloat.animateTo(
            targetValue = targetValue ?: lastFinish,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        ).endState.value
    }
    RingWithBackground(color = color, backgroundColor = backgroundColor, value = animateFloat.value)
}

@Composable
private fun RingWithBackground(color: Color, backgroundColor: Color, value: Float) {
    Box {
        Ring(color = backgroundColor, value = 1f)
        Ring(color = color, value = value)
    }
}

@Composable
private fun Ring(color: Color, value: Float) {
    val thickness = 40f
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawArc(
            color = color,
            startAngle = 165f,
            sweepAngle = 210f * value,
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
package com.example.endeavor.ui.battle

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.BattleQuery
import com.example.endeavor.ui.VitalRing
import com.example.endeavor.ui.theme.Theme

@Composable
fun BattleCreature(creature: BattleQuery.Creature, hitpoints: Int) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 16.dp)
    ) {
        CreatureHitpointRing(value = hitpoints, maxValue = creature.maxHitpoints)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Text(
                text = creature.emoji,
                fontSize = 96.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = creature.name,
                color = Theme.colors.onGraySurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "♥️  $hitpoints / ${creature.maxHitpoints}",
                color = Theme.colors.onGraySurface,
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "🗡️  ${creature.strength}",
                color = Theme.colors.onGraySurface,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
fun CreatureHitpointRing(value: Int?, maxValue: Int?) {
    VitalRing(
        label = null,
        color = Theme.colors.hitpoints,
        backgroundColor = Theme.colors.faintHitpoints,
        value = value,
        maxValue = maxValue,
        countLabel = null,
        thickness = 40f,
        full = false
    )
}
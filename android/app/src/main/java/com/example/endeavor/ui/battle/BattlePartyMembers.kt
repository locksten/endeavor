package com.example.endeavor.ui.battle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.BattleQuery
import com.example.endeavor.LocalAuth
import com.example.endeavor.ui.AppLazyColumn
import com.example.endeavor.ui.VitalRing
import com.example.endeavor.ui.theme.Theme


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun BattlePartyMembers(partyMembers: List<BattleQuery.PartyMember>) {
    val loggedInUsername = LocalAuth.current.loggedInUsernameState().value
    Box(Modifier.fillMaxWidth()) {
        AppLazyColumn(topPadding = 0.dp) {
            items(
                partyMembers.sortedWith(
                    compareBy(
                        { it.username != loggedInUsername },
                        { it.username })
                ),
                { it.id }) { BattlePartyMember(it, loggedIn = it.username == loggedInUsername) }
        }
    }
}

@Composable
fun BattlePartyMember(member: BattleQuery.PartyMember, loggedIn: Boolean = false) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.graySurface)
            .padding(start = 16.dp, end = 12.dp)
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = member.username,
            color = Theme.colors.onGraySurface,
            fontWeight = if (loggedIn) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            fontSize = 20.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BattleMemberVitalRing(
                value = member.energy, maxValue = member.maxEnergy,
                color = Theme.colors.energy,
                faintColor = Theme.colors.faintEnergy,
                loggedIn = loggedIn
            )
            BattleMemberVitalRing(
                value = member.hitpoints, maxValue = member.maxHitpoints,
                color = Theme.colors.hitpoints,
                faintColor = Theme.colors.faintHitpoints,
                loggedIn = loggedIn
            )
        }
    }
}

@Composable
fun BattleMemberVitalRing(
    value: Int?,
    maxValue: Int?,
    loggedIn: Boolean,
    color: Color,
    faintColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = "${value ?: 0}",
            color = color,
            fontWeight = if (loggedIn) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            fontSize = 16.sp,
        )
        Spacer(Modifier.width(8.dp))
        VitalRing(
            label = null,
            color = color,
            backgroundColor = faintColor,
            value = value,
            maxValue = maxValue,
            countLabel = null,
            thickness = 16f,
            full = true
        )
    }
}
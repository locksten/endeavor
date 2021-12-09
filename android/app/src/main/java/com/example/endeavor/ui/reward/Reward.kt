package com.example.endeavor.ui.reward

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.ui.theme.Theme

data class Reward(val id: String, val title: String, val price: Int, val createdAt: String)

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun RewardListItem(reward: Reward, gold: Int) {
    var isUpdateDialogOpen by remember { mutableStateOf(false) }
    var isBuyDialogOpen by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.colors.graySurface)
            .combinedClickable(
                onClick = {
                    isBuyDialogOpen = true
                },
                onLongClick = {
                    isUpdateDialogOpen = true
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            reward.title,
            color = Theme.colors.onGraySurface,
            fontSize = 20.sp,
        )
        Text(
            "${reward.price} 💰",
            color = Theme.colors.onGraySurface,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
    if (isUpdateDialogOpen) CUpdateRewardModal(reward) { isUpdateDialogOpen = false }
    if (isBuyDialogOpen) CBuyRewardModal(reward, gold) { isBuyDialogOpen = false }
}
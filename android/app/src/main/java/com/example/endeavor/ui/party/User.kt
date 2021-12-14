package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.ui.theme.Theme

data class User(
    val id: String,
    val username: String,
    val isPartyLeader: Boolean,
    val trophyCount: Int? = null
)

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun UserListItem(user: User, Content: (@Composable () -> Unit)? = null) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.colors.graySurface)
            .padding(start = 16.dp, end = 8.dp)
            .padding(vertical = 8.dp),
    ) {
        Row {
            if (user.isPartyLeader) {
                Text(
                    text = "👑",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            Text(
                text = user.username,
                color = Theme.colors.onGraySurface,
                fontSize = 20.sp,
            )
            Spacer(Modifier.width(16.dp))
            user.trophyCount?.let { trophies ->
                Text(
                    text = "${trophies}️ 🏆",
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.onSurface,
                    fontSize = 20.sp
                )
            }
        }
        Content?.invoke()
    }
}
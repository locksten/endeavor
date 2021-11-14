package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.LocalGQLClient
import com.example.endeavor.MutationComposable
import com.example.endeavor.ui.theme.Theme

data class User(val id: String, val username: String, val isPartyLeader: Boolean)

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
        Text(
            text = "${
                if (user.isPartyLeader) {
                    "👑 "
                } else {
                    ""
                }
            }${user.username}",
            color = Theme.colors.onGraySurface,
            fontSize = 20.sp,
        )
        Content?.invoke()
    }
}
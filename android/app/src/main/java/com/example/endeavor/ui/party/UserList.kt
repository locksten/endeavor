package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.endeavor.ui.AppLazyColumn

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun UserList(users: List<User>) {
    AppLazyColumn {
        items(users.sortedWith(compareBy { it.username }))
        { UserListItem(it) }
    }
}
package com.example.endeavor.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.endeavor.UserSearchQuery
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.theme.EndeavorTheme
import com.example.endeavor.ui.theme.Theme

@Preview(
    name = "Light Mode",
    device = Devices.PIXEL_2
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = Devices.PIXEL_2
)
@Composable
fun UsersPreview() {
    EndeavorTheme {
        UserList(testUsers)
    }
}

val testUsers = listOf(
    UserSearchQuery.UserSearch(id = "1", username = "username", createdAt = "date"),
    UserSearchQuery.UserSearch(id = "2", username = "username2", createdAt = "date")
)

@Composable
fun User(user: UserSearchQuery.UserSearch) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        Arrangement.SpaceBetween,
    ) {
        Text(
            text = user.username,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
        Text(
            text = "id: ${user.id}",
            color = Theme.colors.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
    }
}

@Composable
fun UserList(users: List<UserSearchQuery.UserSearch>) {
    LazyColumn {
        items(users) {
            User(it)
        }
    }
}

@Composable
fun CUserSearchList(searchTerm: String) {
    val users = gqlWatchQuery(UserSearchQuery(searchTerm))?.userSearch
    users?.let { UserList(it) }
}
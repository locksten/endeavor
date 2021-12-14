package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CancelInviteMutation
import com.example.endeavor.InviteesQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.gqlWatchQuery
import com.example.endeavor.ui.AppLazyColumn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CInviteeList() {
    gqlWatchQuery(InviteesQuery())?.me?.invitees?.let { members ->
        Scaffold(floatingActionButton = { FloatingInviteButton() }) {
            InviteeList(members.map {
                User(
                    id = it.id,
                    username = it.username,
                    isPartyLeader = false,
                    trophyCount = null
                )
            })
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun InviteeList(users: List<User>) {
    AppLazyColumn(fabPadding = true) {
        items(users.sortedWith(compareBy { it.username }), { it.id })
        { UserListItem(it) { CCancelInvitationButton(it) } }
    }
}

@Composable
fun CCancelInvitationButton(user: User) {
    MutationComposable { gql, scope ->
        Button(
            onClick = { scope.launch { cancelInvite(gql, user.id) } },
            modifier = Modifier.fillMaxHeight()
        ) { Text("Cancel") }
    }
}

suspend fun cancelInvite(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            CancelInviteMutation(id)
        ).await()
        gql.query(InviteesQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun FloatingInviteButton() {
    var isDialogOpen by remember { mutableStateOf(false) }
    FloatingActionButton(
        onClick = { isDialogOpen = true },
    ) {
        Icon(Icons.Filled.Add, "Invite")
        if (isDialogOpen) CInviteModal { isDialogOpen = false }
    }
}
package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.CancelInviteMutation
import com.example.endeavor.InviteesQuery
import com.example.endeavor.MutationComposable
import com.example.endeavor.gqlWatchQuery
import kotlinx.coroutines.launch

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
                    isPartyLeader = false
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        items(
            users.sortedWith(compareBy { it.username })
        )
        { UserListItem(it) { CCancelInvitationButton(it) } }
        item { Spacer(Modifier.height(80.dp)) }
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
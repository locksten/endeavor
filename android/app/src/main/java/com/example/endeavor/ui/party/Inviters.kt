package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.endeavor.*
import com.example.endeavor.ui.ButtonWithConfirmation
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun CInviterList() {
    gqlWatchQuery(InvitersQuery())?.me?.inviters?.let { members ->
        InviterList(members.map {
            User(
                id = it.id,
                username = it.username,
                isPartyLeader = false
            )
        })
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun InviterList(users: List<User>) {
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
        { UserListItem(it) { CAcceptDeclineInvitationButtons(it) } }
        item { Spacer(Modifier.height(14.dp)) }
    }
}


@ExperimentalComposeUiApi
@Composable
fun CAcceptDeclineInvitationButtons(user: User) {
    MutationComposable { gql, scope ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .fillMaxHeight()
        ) {
            Button(
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxHeight(),
                onClick = { scope.launch { acceptInvite(gql, user.id) } }) { Text("Accept") }
            ButtonWithConfirmation(
                text = "Decline",
                confirmation = "Really",
                onClick = { scope.launch { declineInvite(gql, user.id) } },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxHeight()
            )
        }
    }
}

suspend fun acceptInvite(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            AcceptInviteMutation(id)
        ).await()
        gql.query(InvitersQuery()).await()
        gql.query(PartyMembersQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

suspend fun declineInvite(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            DeclineInviteMutation(id)
        ).await()
        gql.query(InvitersQuery()).await()
        gql.query(PartyMembersQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}
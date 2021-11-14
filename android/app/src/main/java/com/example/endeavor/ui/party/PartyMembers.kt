package com.example.endeavor.ui.party

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
fun CPartyMemberList() {
    val me = gqlWatchQuery(PartyMembersQuery())?.me
    me?.partyMembers?.let { members ->
        PartyMemberList(members.map {
            User(
                id = it.id,
                username = it.username,
                isPartyLeader = it.isPartyLeader
            )
        }, me.user.id, me.user.isPartyLeader)
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@Composable
fun PartyMemberList(users: List<User>, userId: String, isPartyLeader: Boolean) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        item { Spacer(Modifier.height(14.dp)) }
        items(
            users.sortedWith(compareBy { it.username })
        )
        {
            UserListItem(it) {
                if (it.id == userId) {
                    CLeavePartyButton()
                } else if (isPartyLeader) {
                    CRemovePartyMemberButton(it)
                }
            }
        }
        item { Spacer(Modifier.height(14.dp)) }
    }
}

@ExperimentalComposeUiApi
@Composable
fun CRemovePartyMemberButton(user: User) {
    MutationComposable { gql, scope ->
        ButtonWithConfirmation(
            text = "Remove",
            confirmation = "Really",
            onClick = { scope.launch { removePartyMember(gql, user.id) } },
            modifier = Modifier.fillMaxHeight()
        )
    }
}

suspend fun removePartyMember(gql: ApolloClient, id: String) {
    try {
        gql.mutate(
            RemovePartyMemberMutation(id)
        ).await()
        gql.query(PartyMembersQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}

@ExperimentalComposeUiApi
@Composable
fun CLeavePartyButton() {
    MutationComposable { gql, scope ->
        ButtonWithConfirmation(
            text = "Leave",
            confirmation = "Really",
            onClick = { scope.launch { leaveParty(gql) } },
            modifier = Modifier.fillMaxHeight()
        )
    }
}

suspend fun leaveParty(gql: ApolloClient) {
    try {
        gql.mutate(
            LeavePartyMutation()
        ).await()
        gql.query(PartyMembersQuery()).await()
    } catch (e: ApolloNetworkException) {
    }
}
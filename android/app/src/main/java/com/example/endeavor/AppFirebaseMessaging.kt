package com.example.endeavor

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch

var globalAppMessageHandler: ((RemoteMessage) -> Unit)? = null
var globalFirebaseToken: String? = null

class AppFirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("rova", "globalToken = $token")
        globalFirebaseToken = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        globalAppMessageHandler?.invoke(remoteMessage)
    }
}

@Composable
fun FirebaseTokenRegistration(Content: @Composable () -> Unit) {
    MutationComposable { gql, scope ->
        LaunchedEffect(true) {
            getFirebaseToken {
                scope.launch {
                    updateFirebaseToken(gql, it)
                }
            }
        }
        Content()
    }
}

suspend fun updateFirebaseToken(gql: ApolloClient, token: String) {
    try {
        gql.mutate(UpdateFirebaseTokenMutation(Input.fromNullable(token))).await()
    } catch (e: ApolloNetworkException) {
    }
}


fun getFirebaseToken(onGet: (String) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(
        OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "rova",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }
            Log.d("rova", "Fetching FCM registration token succeeded: ${task.result}")
            task.result?.let { token ->
                Log.d("rova", "globalToken = $token")
                globalFirebaseToken = token
                onGet(token)
            }
        })
}
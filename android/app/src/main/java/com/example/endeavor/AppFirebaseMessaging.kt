package com.example.endeavor

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch

var globalAppMessageHandler: ((RemoteMessage) -> Unit)? = null

class AppFirebaseMessaging : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("rova", "newToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        globalAppMessageHandler?.invoke(remoteMessage)
    }
}

@Composable
fun FirebaseTokenRegistration(Content: @Composable () -> Unit) {
    MutationComposable { gql, scope ->
        LaunchedEffect(true) {
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
                        scope.launch {
                            updateFirebaseToken(gql, token)
                        }
                    }
                })
        }
        Content()
    }
}

suspend fun updateFirebaseToken(gql: ApolloClient, token: String) {
    try {
        gql.mutate(UpdateFirebaseTokenMutation(token)).await()
    } catch (e: ApolloNetworkException) {
    }
}
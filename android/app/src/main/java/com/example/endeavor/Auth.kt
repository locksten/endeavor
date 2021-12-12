package com.example.endeavor

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(private val auth: Authentication) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = auth.getAuthToken()
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

class Authentication(private val context: Context) {
    private val authToken = MutableStateFlow(getPersistedAuthToken())
    private val username = MutableStateFlow(getPersistedUsername())

    @Composable
    fun authTokenState(): State<String?> {
        return authToken.collectAsState()
    }

    @Composable
    fun loggedInUsernameState(): State<String?> {
        return username.collectAsState()
    }

    @Composable
    fun isLoggedIn(): State<Boolean?> {
        return (authToken.map {
            it != null
        }).collectAsState(null)
    }


    suspend fun register(client: ApolloClient, username: String, password: String): String? {
        val response = try {
            client.mutate(RegisterMutation(username, password)).await().data?.register
        } catch (e: ApolloNetworkException) {
            return "Could not connect to the server"
        }
        val success = response?.asSuccessfulLoginResult
        val failure = response?.asFailedRegistrationResult

        val error = when {
            failure?.reason != null -> {
                failure.reason
            }
            success != null -> {
                setAuthToken(success.authTokens.accessToken)
                setUsername(success.user.username)
                null
            }
            else -> {
                "Unknown error"
            }
        }
        if (error != null) {
            return error
        }

        Log.d("rova", "gql updating token, globalToken: $globalFirebaseToken")
        globalFirebaseToken?.let {
            client.mutate(UpdateFirebaseTokenMutation(Input.optional(it))).await()
        }

        return null
    }

    suspend fun logIn(client: ApolloClient, username: String, password: String): String? {
        val response = try {
            client.mutate(LogInMutation(username, password)).await().data?.login
        } catch (e: ApolloNetworkException) {
            return "Could not connect to the server"
        }
        val success = response?.asSuccessfulLoginResult
        val failure = response?.asFailedLoginResult

        val error = when {
            failure?.reason != null -> {
                failure.reason
            }
            success != null -> {
                setAuthToken(success.authTokens.accessToken)
                setUsername(success.user.username)
                null
            }
            else -> {
                "Unknown error"
            }
        }
        if (error != null) {
            return error
        }

        Log.d("rova", "gql updating token, globalToken: $globalFirebaseToken")
        globalFirebaseToken?.let {
            client.mutate(UpdateFirebaseTokenMutation(Input.optional(it))).await()
        }

        return null
    }

    suspend fun logOut(client: ApolloClient) {
        Log.d("rova", "gql updating user token to null, globalToken: $globalFirebaseToken")
        client.mutate(UpdateFirebaseTokenMutation(Input.fromNullable(null))).await()

        removeAuthToken()
        removeUsername()
        client.apolloStore.clearAll()
        client.apolloStore.normalizedCache().clearAll()
    }

    private fun getPersistedUsername(): String? {
        return getSharedPrefString(context, PrefKey.Username)
    }

    private fun setUsername(username: String?) {
        setSharedPrefString(context, PrefKey.Username, username)
        this.username.value = username
    }

    private fun removeUsername() {
        removeSharedPref(context, PrefKey.Username)
        username.value = null
    }

    private fun getPersistedAuthToken(): String? {
        return getSharedPrefString(context, PrefKey.AuthToken)
    }

    fun getAuthToken(): String? {
        return authToken.value
    }

    private fun setAuthToken(token: String?) {
        setSharedPrefString(context, PrefKey.AuthToken, token)
        authToken.value = token
    }

    private fun removeAuthToken() {
        removeSharedPref(context, PrefKey.AuthToken)
        authToken.value = null
    }

}
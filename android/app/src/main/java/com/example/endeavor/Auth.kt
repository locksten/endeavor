package com.example.endeavor

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloNetworkException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
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

        return when {
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
    }

    suspend fun logIn(client: ApolloClient, username: String, password: String): String? {
        val response = try {
            client.mutate(LogInMutation(username, password)).await().data?.login
        } catch (e: ApolloNetworkException) {
            return "Could not connect to the server"
        }
        val success = response?.asSuccessfulLoginResult
        val failure = response?.asFailedLoginResult

        return when {
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
    }

    fun logOut() {
        removeAuthToken()
        removeUsername()
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
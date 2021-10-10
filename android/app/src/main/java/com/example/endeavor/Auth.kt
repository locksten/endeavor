package com.example.endeavor

import android.content.Context
import android.util.Log
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

const val preference_file_key = "com.example.endeavor.PREFERENCE_FILE_KEY"
const val preference_auth_token_key = "com.example.endeavor.AUTH_TOKEN"
const val preference_username_key = "com.example.endeavor.USERNAME"

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
        setAuthToken(null)
        setUsername(null)
    }

    private fun getPersistedUsername(): String? {
        return context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE)
            .getString(preference_username_key, null)
    }

    private fun setUsername(username: String?) {
        val sharedPref = context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(preference_username_key, username)
            apply()
        }
        this.username.value = username
    }

    private fun getPersistedAuthToken(): String? {
        return context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE)
            .getString(preference_auth_token_key, null)
    }

    fun getAuthToken(): String? {
        return authToken.value
    }

    private fun setAuthToken(token: String?) {
        val sharedPref = context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(preference_auth_token_key, token)
            apply()
        }
        authToken.value = token
    }

}
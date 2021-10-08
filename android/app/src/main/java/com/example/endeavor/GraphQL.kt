package com.example.endeavor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.withFetchPolicy
import com.apollographql.apollo3.cache.normalized.withNormalizedCache
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.ApolloClientAwarenessInterceptor
import com.apollographql.apollo3.network.http.HttpNetworkTransport

class Gql constructor(private val client: ApolloClient) {
    suspend fun <D : Query.Data> query(query: Query<D>): D? {
        val response = try {
            Result.success(client.query(ApolloRequest(query).withFetchPolicy(FetchPolicy.CacheFirst)))
        } catch (e: ApolloException) {
            return null
        }.getOrNull()
        return if (response != null && !response.hasErrors()) {
            response.data
        } else {
            null
        }
    }
}

val LocalGQL = staticCompositionLocalOf<Gql> { null!! }

@Composable
fun EndeavorGQL(content: @Composable () -> Unit) {
    val cacheFactory = SqlNormalizedCacheFactory(LocalContext.current)
    val apolloClient = ApolloClient(
        networkTransport = HttpNetworkTransport(
            serverUrl = "http://192.168.0.118:4000/graphql",
            interceptors = listOf(
                ApolloClientAwarenessInterceptor(
                    BuildConfig.APPLICATION_ID,
                    BuildConfig.VERSION_NAME
                )
            ),
        ),
    ).withNormalizedCache(cacheFactory)

    val gql = Gql(apolloClient)

    CompositionLocalProvider(
        LocalGQL provides gql
    ) {
        content()
    }
}
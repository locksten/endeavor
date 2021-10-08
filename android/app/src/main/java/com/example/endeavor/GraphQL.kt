package com.example.endeavor

import androidx.compose.runtime.*
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
        return try {
            val response =
                client.query(ApolloRequest(query).withFetchPolicy(FetchPolicy.CacheFirst))
            if (!response.hasErrors()) {
                response.data
            } else null
        } catch (e: ApolloException) {
            null
        }
    }
}

val LocalGQL = staticCompositionLocalOf<Gql> { null!! }
val LocalGQLClient = staticCompositionLocalOf<ApolloClient> { null!! }

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
        LocalGQL provides gql,
        LocalGQLClient provides apolloClient
    ) {
        content()
    }
}

@Composable
fun <D : Query.Data> gqlProduce(query: Query<D>): State<D?> {
    val client = LocalGQLClient.current
    return produceState<D?>(null, query) {
        value = try {
            val response = client.query(ApolloRequest(query).withFetchPolicy(FetchPolicy.CacheFirst))
            if (!response.hasErrors()) {
                response.data
            } else null
        } catch (e: ApolloException) {
            null
        }
    }
}
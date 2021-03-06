package com.example.endeavor

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import okhttp3.OkHttpClient


val LocalGQLClient = staticCompositionLocalOf<ApolloClient> { null!! }
val LocalAuth = staticCompositionLocalOf<Authentication> { null!! }

@Composable
fun EndeavorGQL(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val authentication = Authentication(context)
    val cacheFactory = SqlNormalizedCacheFactory(LocalContext.current)
    val apolloClient =
        ApolloClient.builder().defaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
            .serverUrl("http://192.168.0.118:4000/graphql")
            .okHttpClient(
                OkHttpClient.Builder()
                    .addInterceptor(AuthorizationInterceptor(authentication))
                    .build()
            )
            .normalizedCache(
                cacheFactory
            ).build()

    CompositionLocalProvider(
        LocalGQLClient provides apolloClient,
        LocalAuth provides authentication
    ) {
        content()
    }
}

@Composable
fun <D : Operation.Data, T, V : Operation.Variables> gqlWatchQuery(query: Query<D, T, V>): T? {
    val gql = LocalGQLClient.current
    return remember { gql.query(query).watcher().toFlow().catch {} }
        .collectAsState(initial = null).value?.data
}

@Composable
fun MutationComposable(Composable: @Composable ((gql: ApolloClient, scope: CoroutineScope) -> Unit)) {
    val scope = rememberCoroutineScope()
    val gql = LocalGQLClient.current
    Composable(gql, scope)
}
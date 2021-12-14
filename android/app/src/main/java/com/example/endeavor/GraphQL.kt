package com.example.endeavor

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.endeavor.type.CustomType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import okhttp3.OkHttpClient
import java.text.ParseException
import java.time.OffsetDateTime


val LocalGQLClient = staticCompositionLocalOf<ApolloClient> { null!! }
val LocalAuth = staticCompositionLocalOf<Authentication> { null!! }

@Composable
fun EndeavorGQL(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val authentication = Authentication(context)
    val cacheFactory = SqlNormalizedCacheFactory(LocalContext.current)

    val dateCustomTypeAdapter = object : CustomTypeAdapter<OffsetDateTime> {
        override fun decode(value: CustomTypeValue<*>): OffsetDateTime {
            return try {
                OffsetDateTime.parse(value.value.toString())
            } catch (e: ParseException) {
                OffsetDateTime.now()
                throw RuntimeException(e)
            }
        }

        override fun encode(value: OffsetDateTime): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value.toString())
        }
    }

    val apolloClient =
        ApolloClient.builder()
            .addCustomTypeAdapter(CustomType.DATE, dateCustomTypeAdapter)
            .defaultResponseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
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

@ExperimentalCoroutinesApi
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
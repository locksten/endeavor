package com.example.endeavor

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.ApolloClientAwarenessInterceptor
import com.apollographql.apollo3.network.http.HttpNetworkTransport

val gql = ApolloClient(
    networkTransport = HttpNetworkTransport(
        serverUrl = "http://192.168.0.118:4000/graphql",
        interceptors = listOf(ApolloClientAwarenessInterceptor(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME))
    )
)

suspend fun <D : Query.Data> gqlQuery(query: Query<D>): D? {
    val response = try {
        Result.success(gql.query(query))
    } catch (e: ApolloException) {
        return null
    }.getOrNull()

    return if (response != null && !response.hasErrors()) {
        response.data
    } else {
        null
    }
}
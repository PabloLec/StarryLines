package api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

object GraphQLClient {
    private val token = System.getenv("GITHUB_TOKEN")!!

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val builder: Request.Builder = original.newBuilder().method(original.method, original.body)
            builder.header("Authorization", "bearer $token")
            chain.proceed(builder.build())
        }
        .build()

    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .okHttpClient(okHttpClient)
        .build()

    fun getClient(): ApolloClient = apolloClient
}
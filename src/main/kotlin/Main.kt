import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import dev.pablolec.starrylines.RepoFilesQuery
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

suspend fun main(args: Array<String>) {
    println("Program arguments: ${args.joinToString()}")

    val token = System.getenv("GITHUB_TOKEN")!!

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original: Request = chain.request()
            val builder: Request.Builder = original.newBuilder().method(original.method, original.body)
            builder.header("Authorization", "bearer $token")
            chain.proceed(builder.build())
        }
        .build()

    val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .okHttpClient(okHttpClient)
        .build()

    val response = apolloClient.query(RepoFilesQuery("pablolec", "recoverpy")).execute()
    print(response.data)
}
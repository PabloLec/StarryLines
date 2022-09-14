package test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import main
import org.junit.jupiter.api.Test

internal class MainTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun parseFetch() = runTest {
        main(arrayOf("fetch", "javascript"))
    }
}

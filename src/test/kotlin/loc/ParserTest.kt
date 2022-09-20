package loc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ParserTest {

    @Test
    fun parseCStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("TestParser.kt")?.bufferedReader()
            ?.let { parseCStyle(it) }

        assertEquals(80, result)
    }

    @Test
    fun parsePythonStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.py")?.bufferedReader()
            ?.let { parsePythonStyle(it) }

        assertEquals(105, result)
    }
}

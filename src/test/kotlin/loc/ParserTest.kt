package loc

import loc.parser.parseCStyle
import loc.parser.parsePythonStyle
import loc.parser.parseShellStyle
import loc.parser.parseSwiftStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ParserTest {

    @Test
    fun testParseCStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("TestParser.kt")?.bufferedReader()
            ?.let { parseCStyle(it) }

        assertEquals(80, result)
    }

    @Test
    fun testParsePythonStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.py")?.bufferedReader()
            ?.let { parsePythonStyle(it) }

        assertEquals(105, result)
    }

    @Test
    fun testParseShellStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.sh")?.bufferedReader()
            ?.let { parseShellStyle(it) }

        assertEquals(29, result)
    }

    @Test
    fun testParseSwiftStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("TestParser.swift")?.bufferedReader()
            ?.let { parseSwiftStyle(it) }

        assertEquals(77, result)
    }
}

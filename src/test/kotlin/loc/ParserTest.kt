package loc

import loc.parser.parseCStyle
import loc.parser.parsePowershellStyle
import loc.parser.parsePythonStyle
import loc.parser.parseRubyStyle
import loc.parser.parseShellStyle
import loc.parser.parseSwiftStyle
import models.LocParseResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ParserTest {

    @Test
    fun testParseCStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("TestParser.kt")?.bufferedReader()
            ?.let { parseCStyle(it) }

        assertEquals(LocParseResult(6, 80), result)
    }

    @Test
    fun testParsePythonStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.py")?.bufferedReader()
            ?.let { parsePythonStyle(it) }

        assertEquals(LocParseResult(13, 138), result)
    }

    @Test
    fun testParseShellStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.sh")?.bufferedReader()
            ?.let { parseShellStyle(it) }

        assertEquals(LocParseResult(1, 29), result)
    }

    @Test
    fun testParsePowerShellStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.ps1")?.bufferedReader()
            ?.let { parsePowershellStyle(it) }

        assertEquals(LocParseResult(2, 40), result)
    }

    @Test
    fun testParseSwiftStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("TestParser.swift")?.bufferedReader()
            ?.let { parseSwiftStyle(it) }

        assertEquals(LocParseResult(8, 77), result)
    }

    @Test
    fun testParseRubyStyle() {
        val result = Thread.currentThread().contextClassLoader.getResourceAsStream("test_parser.rb")?.bufferedReader()
            ?.let { parseRubyStyle(it) }

        assertEquals(LocParseResult(2, 39), result)
    }
}

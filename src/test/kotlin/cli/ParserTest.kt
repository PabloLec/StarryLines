package test.cli

import cli.Action
import cli.parseArgs
import models.Language
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class CLITest {
    @Test
    fun testParseEmpty() {
        assertFails { parseArgs(arrayOf()) }
    }

    @Test
    fun testParseUnknownLanguage() {
        assertFails { parseArgs(arrayOf("test", "unknown")) }
    }

    @Test
    fun testParseFetch() {
        val expected = Action.FETCH.also {
            it.args.addAll(
                setOf(Language.JAVASCRIPT, Language.JAVA, Language.KOTLIN)
            )
        }

        assertEquals(expected, parseArgs(arrayOf("fetch", "javascript", "java", "kotlin")))
    }

    @Test
    fun testParseFetchAll() {
        val expected = Action.FETCH.also { it.args.addAll(Language.values()) }

        assertEquals(expected, parseArgs(arrayOf("fetch", "all")))
    }

    @Test
    fun testParseFetchEmpty() {
        assertFails { parseArgs(arrayOf("fetch")) }
    }

    @Test
    fun testParseFetchUnknownLanguage() {
        assertFails { parseArgs(arrayOf("fetch", "java", "unknown")) }
    }

    @Test
    fun testParseGetLoc() {
        val expected = Action.GETLOC.also {
            it.args.addAll(
                setOf(Language.JAVASCRIPT, Language.JAVA, Language.KOTLIN)
            )
        }

        assertEquals(expected, parseArgs(arrayOf("getloc", "javascript", "java", "kotlin")))
    }

    @Test
    fun testParseGetLocAll() {
        val expected = Action.GETLOC.also { it.args.addAll(Language.values()) }
        assertEquals(expected, parseArgs(arrayOf("getloc", "all")))
    }

    @Test
    fun testParseGetLocEmpty() {
        assertFails { parseArgs(arrayOf("getloc")) }
    }

    @Test
    fun testParseGetLocUnknownLanguage() {
        assertFails { parseArgs(arrayOf("getloc", "java", "unknown")) }
    }

    @Test
    fun testParseTop() {
        val expected = Action.TOP.also {
            it.args.addAll(
                setOf(Language.JAVASCRIPT, Language.JAVA, Language.KOTLIN)
            )
        }
        assertEquals(expected, parseArgs(arrayOf("top", "javascript", "java", "kotlin")))
    }

    @Test
    fun testParseTopAll() {
        val expected = Action.TOP.also { it.args.addAll(Language.values()) }
        assertEquals(expected, parseArgs(arrayOf("top", "all")))
    }

    @Test
    fun testParseTopEmpty() {
        assertFails { parseArgs(arrayOf("top")) }
    }

    @Test
    fun testParseTopUnknownLanguage() {
        assertFails { parseArgs(arrayOf("top", "java", "unknown")) }
    }
}

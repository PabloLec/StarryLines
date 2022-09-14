package test

import Action
import models.SupportedLanguage
import org.junit.jupiter.api.Test
import parseArgs
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class CLITest {
    @Test
    fun parseUnknownLanguage() {
        assertFails { parseArgs(arrayOf("test", "unknown")) }
    }

    @Test
    fun parseFetch() {
        val expected = Action.FETCH.also { it.args.addAll(setOf("javascript", "java", "kotlin")) }
        assertEquals(expected, parseArgs(arrayOf("fetch", "javascript", "java", "kotlin")))
    }

    @Test
    fun parseFetchAll() {
        val expected = Action.FETCH.also { it.args.addAll(SupportedLanguage.values().map { it.name.lowercase() }) }
        assertEquals(expected, parseArgs(arrayOf("fetch", "all")))
    }

    @Test
    fun parseFetchEmpty() {
        assertFails { parseArgs(arrayOf("fetch")) }
    }

    @Test
    fun parseFetchUnknownLanguage() {
        assertFails { parseArgs(arrayOf("fetch", "java", "unknown")) }
    }

    @Test
    fun parseGetLoc() {
        val expected = Action.GETLOC.also { it.args.addAll(setOf("javascript", "java", "kotlin")) }
        assertEquals(expected, parseArgs(arrayOf("getloc", "javascript", "java", "kotlin")))
    }

    @Test
    fun parseGetLocAll() {
        val expected = Action.GETLOC.also { it.args.addAll(SupportedLanguage.values().map { it.name.lowercase() }) }
        assertEquals(expected, parseArgs(arrayOf("getloc", "all")))
    }

    @Test
    fun parseGetLocEmpty() {
        assertFails { parseArgs(arrayOf("getloc")) }
    }

    @Test
    fun parseGetLocUnknownLanguage() {
        assertFails { parseArgs(arrayOf("getloc", "java", "unknown")) }
    }
}

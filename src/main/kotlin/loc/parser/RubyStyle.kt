package loc.parser

import java.io.BufferedReader

fun parseRubyStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var isInComment = false
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("=begin") -> isInComment = true
            isInComment && line!!.startsWith("=end") -> isInComment = false
            isInComment -> continue
            else -> parsedLength += removeInlineCommentsShellStyle(line!!).length
        }
    }
    return parsedLength
}
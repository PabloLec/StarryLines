package loc.parser

import java.io.BufferedReader

fun parsePowershellStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var isInComment = false
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line!!.isEmpty() -> continue
            line!!.startsWith("<#") && line!!.endsWith("#>") -> continue
            isInComment && line!!.contains("#>") -> isInComment = false
            line!!.startsWith("<#") -> isInComment = true
            isInComment -> continue
            line!!.startsWith("#") -> continue
            else -> parsedLength += removeInlineCommentsShellStyle(line!!).length
        }
    }
    return parsedLength
}
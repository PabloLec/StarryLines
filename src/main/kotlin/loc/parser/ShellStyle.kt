package loc.parser

import models.LocParseResult
import java.io.BufferedReader

fun parseShellStyle(reader: BufferedReader): LocParseResult {
    var line: String?
    var lineCount = 0
    var parsedLength = 0
    var isInComment = false
    var commentMarker: String? = null

    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line!!.isEmpty() -> continue
            line!!.contains("<<") -> {
                commentMarker = line!!.substring(line!!.indexOf("<<") + 2).trim().replace("'", "")
                isInComment = true
            }
            isInComment && line!! == commentMarker -> {
                isInComment = false
                commentMarker = null
            }
            line!!.startsWith(":") && line!!.endsWith("'") && line!!.count { it == '\'' } % 2 == 0 -> continue
            isInComment && (line!!.contains("'")) -> isInComment = false
            line!!.startsWith(":") -> isInComment = true
            isInComment -> continue
            line!!.startsWith("#") -> continue
            else -> {
                val lineLength = removeInlineCommentsShellStyle(line!!).length
                parsedLength += lineLength
                if (lineLength > 0) {
                    lineCount += 1
                }
            }
        }
    }
    return LocParseResult(lineCount, parsedLength)
}

fun removeInlineCommentsShellStyle(line: String): String {
    val delimiter = line.lastIndexOf("#")
    if (delimiter != -1 && (line.substring(0, delimiter).count { it == '"' } % 2) == 0) {
        return line.substring(0, delimiter).trim()
    }
    return line.trim()
}

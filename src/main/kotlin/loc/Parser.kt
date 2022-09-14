package loc

import java.io.BufferedReader

private fun removeInlineCommentJavaStyle(line: String): String {
    val start = line.indexOf("/*")
    val end = line.indexOf("*/")
    if (start != -1 && end != -1) {
        return line.substring(0, start) + line.substring(end + 2)
    }
    val delimiter = line.lastIndexOf("//")
    if (delimiter != -1 && (line.substring(0, delimiter).count { it == '"' } % 2) == 0) {
        return line.substring(0, delimiter).trim()
    }
    return line.trim()
}

private fun removeInlineCommentPythonStyle(line: String): String {
    val delimiter = line.lastIndexOf("#")
    if (delimiter != -1 && (line.substring(0, delimiter).count { it == '"' } % 2) == 0) {
        return line.substring(0, delimiter).trim()
    }
    return line.trim()
}

fun parseJavaStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var isInComment = false
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("/*") && line!!.endsWith("*/") -> continue
            line!!.startsWith("/*") -> isInComment = true
            isInComment && line!!.contains("*/") -> isInComment = false
            isInComment -> continue
            line!!.startsWith("//") -> continue
            else -> parsedLength += removeInlineCommentJavaStyle(line!!).length
        }
    }
    return parsedLength
}

fun parsePythonStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var isInComment = false
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line.isNullOrEmpty() -> continue
            line!!.startsWith("\"\"\"") && line!!.endsWith("\"\"\"") -> continue
            line!!.startsWith("'''") && line!!.endsWith("'''") -> continue
            line!!.startsWith("\"\"\"") || line!!.startsWith("'''") -> isInComment = true
            isInComment && (line!!.contains("\"\"\"") || line!!.startsWith("'''")) -> isInComment = false
            isInComment -> continue
            line!!.startsWith("#") -> continue
            else -> parsedLength += removeInlineCommentPythonStyle(line!!).length
        }
    }
    return parsedLength
}

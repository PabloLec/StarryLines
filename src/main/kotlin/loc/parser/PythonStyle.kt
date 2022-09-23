package loc

import java.io.BufferedReader

fun parsePythonStyle(reader: BufferedReader): Int {
    var line: String?
    var parsedLength = 0

    var isInComment = false
    while (reader.readLine().also { line = it } != null) {
        line = line!!.trim()
        when {
            line!!.isEmpty() -> continue
            line!!.startsWith("\"\"\"") && line!!.endsWith("\"\"\"") -> continue
            isInComment && (line!!.contains("\"\"\"") || line!!.contains("'''")) -> isInComment = false
            line!!.startsWith("'''") && line!!.endsWith("'''") -> continue
            line!!.startsWith("\"\"\"") || line!!.startsWith("'''") -> isInComment = true
            isInComment -> continue
            line!!.startsWith("#") -> continue
            else -> parsedLength += removeInlineComments(line!!).length
        }
    }
    return parsedLength
}

private fun removeInlineComments(line: String): String {
    val delimiter = line.lastIndexOf("#")
    if (delimiter != -1 && (line.substring(0, delimiter).count { it == '"' } % 2) == 0) {
        return line.substring(0, delimiter).trim()
    }
    return line.trim()
}

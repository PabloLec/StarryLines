package models

import loc.parseCStyle
import loc.parsePythonStyle
import loc.parser.parseSwiftStyle
import java.io.BufferedReader

enum class SupportedLanguage {
    JAVASCRIPT {
        override fun extensions() = setOf(".js", ".jsx")
        override fun commentParser() = ::parseCStyle
    },
    TYPESCRIPT {
        override fun extensions() = setOf(".ts", ".tsx")
        override fun commentParser() = ::parseCStyle
    },
    KOTLIN {
        override fun extensions() = setOf(".kt", ".kts")
        override fun commentParser() = ::parseCStyle
    },
    JAVA {
        override fun extensions() = setOf(".java")
        override fun commentParser() = ::parseCStyle
    },
    C {
        override fun extensions() = setOf(".c", ".h")
        override fun commentParser() = ::parseCStyle
    },
    CPP {
        override fun extensions() = setOf(".cpp", ".cc", ".cxx", ".c++", ".hpp", ".hh", ".hxx", ".h++")
        override fun commentParser() = ::parseCStyle
    },
    CSHARP {
        override fun extensions() = setOf(".cs", ".csx")
        override fun commentParser() = ::parseCStyle
    },
    GO {
        override fun extensions() = setOf(".go", ".golang")
        override fun commentParser() = ::parseCStyle
    },
    PYTHON {
        override fun extensions() = setOf(".py", ".pyw")
        override fun commentParser() = ::parsePythonStyle
    },
    SWIFT {
        override fun extensions() = setOf(".swift")
        override fun commentParser() = ::parseSwiftStyle
    };

    abstract fun extensions(): Set<String>
    abstract fun commentParser(): (BufferedReader) -> Int
}

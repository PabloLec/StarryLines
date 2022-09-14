package models

import loc.parseCStyle
import loc.parsePythonStyle
import java.io.BufferedReader

enum class SupportedLanguage {
    JAVASCRIPT {
        override fun extensions() = setOf(".js")
        override fun commentParser() = ::parseCStyle
    },
    TYPESCRIPT {
        override fun extensions() = setOf(".ts")
        override fun commentParser() = ::parseCStyle
    },
    KOTLIN {
        override fun extensions() = setOf(".kt")
        override fun commentParser() = ::parseCStyle
    },
    JAVA {
        override fun extensions() = setOf(".java")
        override fun commentParser() = ::parseCStyle
    },
    C {
        override fun extensions() = setOf(".c")
        override fun commentParser() = ::parseCStyle
    },
    CPP {
        override fun extensions() = setOf(".cpp")
        override fun commentParser() = ::parseCStyle
    },
    CSHARP {
        override fun extensions() = setOf(".cs")
        override fun commentParser() = ::parseCStyle
    },
    GO {
        override fun extensions() = setOf(".go")
        override fun commentParser() = ::parseCStyle
    },
    PYTHON {
        override fun extensions() = setOf(".py")
        override fun commentParser() = ::parsePythonStyle
    };

    abstract fun extensions(): Set<String>
    abstract fun commentParser(): (BufferedReader) -> Int
}

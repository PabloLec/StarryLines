package models

import loc.parseJavaStyle
import loc.parsePythonStyle
import java.io.BufferedReader

enum class SupportedLanguage {
    JAVASCRIPT {
        override fun extensions() = setOf(".js")
        override fun commentParser() = ::parseJavaStyle
    },
    TYPESCRIPT {
        override fun extensions() = setOf(".ts")
        override fun commentParser() = ::parseJavaStyle
    },
    KOTLIN {
        override fun extensions() = setOf(".kt")
        override fun commentParser() = ::parseJavaStyle
    },
    JAVA {
        override fun extensions() = setOf(".java")
        override fun commentParser() = ::parseJavaStyle
    },
    C {
        override fun extensions() = setOf(".c")
        override fun commentParser() = ::parseJavaStyle
    },
    CPP {
        override fun extensions() = setOf(".cpp")
        override fun commentParser() = ::parseJavaStyle
    },
    GO {
        override fun extensions() = setOf(".go")
        override fun commentParser() = ::parseJavaStyle
    },
    PYTHON {
        override fun extensions() = setOf(".py")
        override fun commentParser() = ::parsePythonStyle
    };

    abstract fun extensions(): Set<String>
    abstract fun commentParser(): (BufferedReader) -> Int
}

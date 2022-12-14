package models

import loc.parser.parseCStyle
import loc.parser.parsePowershellStyle
import loc.parser.parsePythonStyle
import loc.parser.parseRubyStyle
import loc.parser.parseShellStyle
import loc.parser.parseSwiftStyle
import java.io.BufferedReader

enum class Language {
    JAVASCRIPT {
        override fun extensions() = setOf(".js", ".jsx", ".mjs", ".cjs")
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
        override fun extensions() = setOf(".c", ".cpp", ".cc", ".cxx", ".c++", ".h", ".hpp", ".hh", ".hxx", ".h++", ".hop")
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
    RUST {
        override fun extensions() = setOf(".rs", ".rust")
        override fun commentParser() = ::parseCStyle
    },
    DART {
        override fun extensions() = setOf(".dart", ".dartlang", ".dart-lang")
        override fun commentParser() = ::parseCStyle
    },
    PYTHON {
        override fun extensions() = setOf(".py", ".pyw")
        override fun commentParser() = ::parsePythonStyle
    },
    SWIFT {
        override fun extensions() = setOf(".swift")
        override fun commentParser() = ::parseSwiftStyle
    },
    SHELL {
        override fun extensions() = setOf(".sh", ".bash", ".zsh", ".fish", ".ksh", ".csh", ".tcsh")
        override fun commentParser() = ::parseShellStyle
    },
    RUBY {
        override fun extensions() = setOf(".rb", ".rbw")
        override fun commentParser() = ::parseRubyStyle
    },
    PHP {
        override fun extensions() = setOf(".php", ".php3", ".php4", ".php5", ".php7", ".phtml")
        override fun commentParser() = ::parseCStyle
    },
    SCALA {
        override fun extensions() = setOf(".scala", ".sc")
        override fun commentParser() = ::parseCStyle
    },
    POWERSHELL {
        override fun extensions() = setOf(".ps1", ".psm1", ".psd1")
        override fun commentParser() = ::parsePowershellStyle
    };

    abstract fun extensions(): Set<String>
    abstract fun commentParser(): (BufferedReader) -> LocParseResult

    override fun toString() = name.lowercase()

    companion object {
        fun find(value: String): Language? = Language.values().find { it.toString() == value }
    }
}

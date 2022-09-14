package models

enum class SupportedLanguage {
    JAVASCRIPT {
        override fun extensions() = setOf(".js")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    TYPESCRIPT {
        override fun extensions() = setOf(".ts")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    KOTLIN {
        override fun extensions() = setOf(".kt")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    JAVA {
        override fun extensions() = setOf(".java")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    C {
        override fun extensions() = setOf(".c")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    CPP {
        override fun extensions() = setOf(".cpp")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    GO {
        override fun extensions() = setOf(".go")
        override fun commentRegex() = Regex("""/\*(.|[\r\n])*?\*/|[^:]//.*""", RegexOption.MULTILINE)
    },
    PYTHON {
        override fun extensions() = setOf(".py")
        override fun commentRegex() = Regex("""[^:]#.*|([^(.]\"\"\"[^(]*)\"\"\"""", RegexOption.MULTILINE)
    };

    abstract fun extensions(): Set<String>
    abstract fun commentRegex(): Regex
}

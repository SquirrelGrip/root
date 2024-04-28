package com.github.squirrelgrip.extension.collection

import org.antlr.v4.runtime.*

object StringCompiler : Compiler<String>() {
    override fun matches(
        control: String,
        candidate: String
    ): Boolean =
        control == candidate

    override fun matchesRegex(regex: Regex, candidate: String): Boolean =
        regex.matches(candidate)
}

object CollectionStringCompiler : Compiler<Collection<String>>() {
    override fun matches(
        control: String,
        candidate: Collection<String>
    ): Boolean =
        control in candidate

    override fun matchesRegex(
        regex: Regex,
        candidate: Collection<String>
    ): Boolean =
        candidate.any {
            regex.matches(it)
        }
}

object SequenceStringCompiler : Compiler<Sequence<String>>() {
    override fun matches(
        control: String,
        candidate: Sequence<String>
    ): Boolean =
        control in candidate

    override fun matchesRegex(
        regex: Regex,
        candidate: Sequence<String>
    ): Boolean =
        candidate.any {
            regex.matches(it)
        }
}

abstract class Compiler<T> {
    private val visitor: DrainerParserBaseVisitor<(T) -> Boolean> =
        object : DrainerParserBaseVisitor<(T) -> Boolean>() {
            override fun visitPredicate(ctx: DrainerParser.PredicateContext): (T) -> Boolean = {
                visit(ctx.expression()).invoke(it)
            }

            override fun visitLiteralExpression(ctx: DrainerParser.LiteralExpressionContext): (T) -> Boolean =
                {
                    ctx.text.uppercase() == "TRUE"
                }

            override fun visitTextExpression(ctx: DrainerParser.TextExpressionContext): (T) -> Boolean =
                {
                    matches(ctx.text, it)
                }

            override fun visitGlobExpression(ctx: DrainerParser.GlobExpressionContext): (T) -> Boolean =
                {
                    matchesGlob(ctx.text, it)
                }

            override fun visitRegexExpression(ctx: DrainerParser.RegexExpressionContext): (T) -> Boolean =
                {
                    matchesRegex(ctx.text, it)
                }

            override fun visitNotExpression(ctx: DrainerParser.NotExpressionContext): (T) -> Boolean =
                {
                    !visit(ctx.expression()).invoke(it)
                }

            override fun visitAndExpression(ctx: DrainerParser.AndExpressionContext): (T) -> Boolean =
                {
                    visit(ctx.expression(0)).invoke(it) && visit(ctx.expression(1)).invoke(it)
                }

            override fun visitOrExpression(ctx: DrainerParser.OrExpressionContext): (T) -> Boolean =
                {
                    visit(ctx.expression(0)).invoke(it) || visit(ctx.expression(1)).invoke(it)
                }

            override fun visitXorExpression(ctx: DrainerParser.XorExpressionContext): (T) -> Boolean =
                {
                    visit(ctx.expression(0)).invoke(it) xor visit(ctx.expression(1)).invoke(it)
                }

            override fun visitImpliesExpression(ctx: DrainerParser.ImpliesExpressionContext): (T) -> Boolean =
                {
                    if (visit(ctx.expression(0)).invoke(it)) {
                        visit(ctx.expression(1)).invoke(it)
                    } else {
                        true
                    }
                }

            override fun visitParenExpression(ctx: DrainerParser.ParenExpressionContext): (T) -> Boolean =
                {
                    visit(ctx.expression()).invoke(it)
                }
        }

    fun matchesGlob(
        globString: String,
        candidate: T
    ): Boolean =
        matchesRegex(globToRegEx(globString), candidate)

    fun matchesRegex(
        regexString: String,
        candidate: T
    ): Boolean =
        matchesRegex(regexString.toRegex(), candidate)

    abstract fun matchesRegex(
        regex: Regex,
        candidate: T
    ): Boolean

    abstract fun matches(
        control: String,
        candidate: T
    ): Boolean

    private fun globToRegEx(glob: String): Regex =
        (glob.foldIndexed("^") { _, acc, next ->
            acc + when (next) {
                '*' -> ".*"
                '?' -> '.'
                '.' -> "\\."
                else -> next
            }
        } + '$').toRegex()

    fun compile(expression: String): (T) -> Boolean =
        if (expression.isNotBlank()) {
            visitor.visit(
                DrainerParser(
                    CommonTokenStream(
                        DrainerLexer(
                            CharStreams.fromString(expression)
                        )
                    )
                ).also {
                    it.addErrorListener(object : BaseErrorListener() {
                        override fun syntaxError(
                            recognizer: Recognizer<*, *>?,
                            offendingSymbol: Any?,
                            line: Int,
                            charPositionInLine: Int,
                            message: String?,
                            recognitionException: RecognitionException?
                        ) {
                            throw Exception("Invalid Expression: $message")
                        }

                    })
                }.predicate()
            )
        } else {
            { false }
        }

    private fun prepare(
        expression: String?,
        aliases: Map<String, String>
    ): ((T) -> Boolean)? =
        expression?.let {
            aliases.asSequence()
                .fold(it) { expression, (variable, value) ->
                    expression.replace(variable, "(${value})")
                }
        }?.let {
            compile(it)
        }
}

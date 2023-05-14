package com.sopan.augmented_reality.vision

internal class MathEvaluation(private val expression: String) {
    private var ch = 0
    private var pos = -1
    private var wrongResult = false
    fun parse(): String {
        nextChar()
        val x = parseLowArithmetic()
        if (pos < expression.length) wrongResult = true
        return if (!wrongResult) x.toString() else "Wrong expression"
    }

    private fun parseLowArithmetic(): Double {
        var x = parseMediumArithmetic()
        while (true) {
            if (evaluate('+'.code)) // addition
                x += parseMediumArithmetic() else if (evaluate('-'.code)) // subtraction
                x -= parseMediumArithmetic() else return x
        }
    }

    private fun parseMediumArithmetic(): Double {
        var x = parseHighArithmetic()
        while (true) {
            if (evaluate('*'.code)) // multiplication
                x *= parseHighArithmetic() else if (evaluate('/'.code)) // division
                x /= parseHighArithmetic() else if (evaluate('%'.code)) // modulus
                x %= parseHighArithmetic() else return x
        }
    }

    private fun parseHighArithmetic(): Double {
        if (evaluate('-'.code)) // unary minus
            return -parseHighArithmetic()
        var x = 0.0
        val startPos = pos
        if (evaluate('('.code)) {
            // parentheses
            x = parseLowArithmetic()
            evaluate(')'.code)
        } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
            // numbers
            while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
            x = expression.substring(startPos, pos).toDouble()
        } else if (ch >= 'a'.code && ch <= 'z'.code) {
            // functions
            while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
            val func = expression.substring(startPos, pos)
            x = parseHighArithmetic()
            when (func) {
                "sqrt" -> x = Math.sqrt(x)
                "sin" -> x = Math.sin(Math.toRadians(x))
                "cos" -> x = Math.cos(Math.toRadians(x))
                "tg" -> x = Math.tan(Math.toRadians(x))
                else -> wrongResult = true
            }
        } else wrongResult = true

        // ^ looks like A, so will not recognize it!
        if (evaluate('^'.code)) x = Math.pow(x, parseHighArithmetic())
        return x
    }

    // get next char element
    private fun nextChar() {
        ch = if (++pos < expression.length) expression[pos].toInt() else -1
    }

    private fun evaluate(charToEvaluate: Int): Boolean {
        while (ch == ' '.code) nextChar()
        if (ch == charToEvaluate) {
            nextChar()
            return true
        }
        return false
    }
}
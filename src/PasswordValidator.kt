import java.io.File
import kotlin.math.log
import kotlin.math.log2

val DOWN_REGISTER: Array<Char> = arrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
)
val UP_REGISTER: Array<Char> = arrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
)
var NUMBERS: Array<Char> = arrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
)
var SPECIAL_SYMBOLS: Array<Char> = arrayOf(
        '-', '_', '.', '$', '#', '@', '!', '^', '*', '+'
)


abstract class Rule() {

    abstract fun checkRule(password: String)

    open val name: String
        get() = "simple"

}
class RuleException(message: String) : Exception() {
    init {
        throw Exception(message)
    }
}
class ValidatorException(message: String) : Exception() {
    init {
        throw Exception(message)
    }
}


class LenRule(_len: Int) : Rule() {
    private val len: Int = _len

    override fun checkRule(password: String) {
        if (password.length < len) {
            throw RuleException("Пароль должен быть не менее $len символов")
        }
    }

}


class RegisterRule() : Rule() {
    override val name: String
        get() = "Register"

    override fun checkRule(password: String) {
        var condition = false
        for (symbol in password) {
            if (DOWN_REGISTER.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Пароль не содержит символы нижнего регистра")
        }
        condition = false
        for (symbol in password) {
            if (UP_REGISTER.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Пароль не содержит символы верхнего регистра")
        }
    }
}


class NumberOrSpecialRule() : Rule() {
    override val name: String
        get() = "NumberOrSpecial"


    override fun checkRule(password: String) {
        var condition = false
        for (symbol in password) {
            if (NUMBERS.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Пароль не содержит цифр")
        }
        condition = false
        for (symbol in password) {
            if (SPECIAL_SYMBOLS.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Пароль не содержит специальных символов")
        }
    }

}


class DictionaryRule(fileName: String) : Rule() {
    override val name: String
        get() = "Dictionary"

    private val dictName: String = fileName
    val file = File(dictName)
    val words = file.readText().split("\n")

    override fun checkRule(password: String) {
        for (word in words) {
            if ((password.contains(word)) && (word != "")){
                throw RuleException("Пароль содержит словарное слово")
            }
        }

    }

}


class EntropyRule() : Rule() {
    override val name: String
        get() = "Entropy"
    var N = 0
    override fun checkRule(password: String) {
        for (symbol in password) {
            if (NUMBERS.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (SPECIAL_SYMBOLS.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (UP_REGISTER.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (DOWN_REGISTER.contains(symbol)) {
                N += NUMBERS.size
                break
            }
        }
        var summ = 0.0
        for (i in 1..N) {
            summ += (1.toDouble() / N) * log2((1.toDouble() / N))
        }
        summ = -summ
        if (summ < 5) {
            throw RuleException("Величина ентропии низка")
        }
    }

}


class PasswordValidator() {

    private var rules: ArrayList<Rule> = ArrayList()

    fun addRule(rule: Rule) {
        rules.add(rule)
    }

    fun checkPassword(password: String) {
        checkRules()
        for (rule in rules) {
            rule.checkRule(password)
        }
    }

    private fun checkRules() {
        if (rules.size == 0) {
            throw ValidatorException("в валидаторе нет правил")
        }
        for (rule in rules) {
            var countEqualRules = 0
            for (rule2 in rules) {
                if (rule2.name == rule.name) {
                    countEqualRules += 1
                }
            }
            if (countEqualRules >= 2) {
                throw ValidatorException("Правила повторяются!!")
            }
        }
    }

}


fun main(args: Array<String>) {
    val password = "Q1-dq1s1w"
    val validator = PasswordValidator()
    validator.addRule(LenRule(5))
    validator.addRule(RegisterRule())
    validator.addRule(NumberOrSpecialRule())
    validator.addRule(DictionaryRule("src/pswd-dict.txt"))
    validator.addRule(EntropyRule())
    validator.checkPassword(password)
}


fun test1() {
    val validator = PasswordValidator()
    validator.addRule(LenRule(5))
    validator.checkPassword("5555555")
    validator.checkPassword("1")
}

fun test2() {
    val validator = PasswordValidator()
    validator.addRule(RegisterRule())
    validator.checkPassword("qweQWE")
    validator.checkPassword("qwe")

}

fun test3() {
    val validator = PasswordValidator()
    validator.addRule(NumberOrSpecialRule())
    validator.checkPassword("qwe-")
    validator.checkPassword("qwe")
}

fun test4() {
    val validator = PasswordValidator()
    validator.addRule(DictionaryRule("src/pswd-dict.txt"))
    validator.checkPassword("qg21")
    validator.checkPassword("run")
}

fun test5() {
    val validator = PasswordValidator()
    validator.addRule(EntropyRule())
    validator.checkPassword("Q1-dq1s1w")
    validator.checkPassword("qqq")
}

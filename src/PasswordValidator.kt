import java.io.File
import kotlin.math.log2


const val DICT_PATH = "src/pswd-dict.txt"

val NUMBERS: Array<Char> = arrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
)

val DOWN_REGISTER: Array<Char> = arrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
)
val UP_REGISTER: Array<Char> = arrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
)
val SPECIAL_SYMBOLS: Array<Char> = arrayOf(
        '-', '_', '.', '$', '#', '@', '!', '^', '*', '+'
)


abstract class Rule {

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
            throw RuleException("Password has has less than $len symbols")
        }
    }

}


class RegisterRule : Rule() {
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
            throw RuleException("Password has no DOWN_REGISTER")
        }
        condition = false
        for (symbol in password) {
            if (UP_REGISTER.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Password has no UP_REGISTER")
        }
    }
}


class NumberOrSpecialRule : Rule() {
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
            throw RuleException("Password has no NUMBERS")
        }
        condition = false
        for (symbol in password) {
            if (SPECIAL_SYMBOLS.contains(symbol)) {
                condition = true
                break
            }
        }
        if (!condition) {
            throw RuleException("Password has no SPECIAL_SYMBOLS")
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
                throw RuleException("dict world in password")
            }
        }

    }

}


class EntropyRule : Rule() {
    override val name: String
        get() = "Entropy"
    var count = 0
    override fun checkRule(password: String) {
        for (symbol in password) {
            if (NUMBERS.contains(symbol)) {
                count += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (SPECIAL_SYMBOLS.contains(symbol)) {
                count += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (UP_REGISTER.contains(symbol)) {
                count += NUMBERS.size
                break
            }
        }
        for (symbol in password) {
            if (DOWN_REGISTER.contains(symbol)) {
                count += NUMBERS.size
                break
            }
        }
        var summ = 0.0
        for (i in 1..count) {
            summ += (1.toDouble() / count) * log2((1.toDouble() / count))
        }
        summ = -summ
        if (summ < 5) {
            throw RuleException("Very low Entropy")
        }
    }

}


class PasswordValidator {

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
            throw ValidatorException("Validator does not any have rules")
        }
        for (rule in rules) {
            var countEqualRules = 0
            for (rule2 in rules) {
                if (rule2.name == rule.name) {
                    countEqualRules += 1
                }
            }
            if (countEqualRules >= 2) {
                throw ValidatorException("2 or more same rules")
            }
        }
    }

}


fun main(args: Array<String>) {
    val password = "Q1-dq1s1w"
    var validator = PasswordValidator()
    validator.addRule(LenRule(5))
    validator.addRule(RegisterRule())
    validator.addRule(NumberOrSpecialRule())
    validator.addRule(DictionaryRule(DICT_PATH))
    validator.addRule(EntropyRule())
    validator.checkPassword(password)
    
    validator = PasswordValidator()
    validator.addRule(LenRule(5))
    validator.checkPassword("5555555")
    validator.checkPassword("1")

    validator = PasswordValidator()
    validator.addRule(RegisterRule())
    validator.checkPassword("qweQWE")
    validator.checkPassword("qwe")

    validator = PasswordValidator()
    validator.addRule(NumberOrSpecialRule())
    validator.checkPassword("qwe-")
    validator.checkPassword("qwe")

    validator = PasswordValidator()
    validator.addRule(DictionaryRule(DICT_PATH))
    validator.checkPassword("qg21")
    validator.checkPassword("run")

    validator = PasswordValidator()
    validator.addRule(EntropyRule())
    validator.checkPassword("Q1-dq1s1w")
    validator.checkPassword("qqq")
}



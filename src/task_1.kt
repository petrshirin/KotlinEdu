import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

val OPERATIONS: HashMap<String, Int> = hashMapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

class StackData(var data: String, var priority: Int, var haveBrackets: Boolean = false) {

    public override fun toString(): String {
        if (haveBrackets && priority != 0) {
            return "($data)"
        }
        else {
            return data
        }
    }
}

fun main() {
    print("Enter line: ")

    val line = readLine()
    val split_line = line?.split(' ')

    try {
        checkInputData(split_line as ArrayList<String>)
    }
    catch (e: Exception) {
        println(e.message)
        return
    }

    println(postToInf(split_line as ArrayList<String>).toString())
}

fun checkInputData(split_line: ArrayList<String>){
    var countOperations: Int = 0
    var countDigits: Int = 0
    for (elem in split_line) {
        if (elem in OPERATIONS.keys) {
            countOperations++
        }
        else {
            try {
                elem.toInt()
                countDigits++
            }
            catch (e: NumberFormatException) {
                throw Exception("Invalid number $elem")
            }

        }
    }
    if (countDigits != countOperations + 1) {
        throw Exception("invalid count arguments")
    }
}


fun postToInf(split_line: ArrayList<String>): StackData {
    val result = StringBuilder()
    val stack:  ArrayDeque<StackData> = ArrayDeque()
    val strings: ArrayList<String> = ArrayList()
    var operand1: StackData
    var operand2: StackData
    var maxPriority: Int = 0
    var i: Int = 0
    while (i < split_line.size) {
        val elem: String = split_line[i]
        if (elem in OPERATIONS.keys) {
            if (stack.size < 2) {
                split_line.add(elem)
                i++
                continue
            }
            operand1 = stack.poll()
            maxPriority = OPERATIONS[elem]!!
            if (operand1.priority < OPERATIONS[elem]!!) {
                operand1.haveBrackets = true
            }
            operand2 = stack.poll()
            if (operand2.priority < OPERATIONS[elem]!!) {
                operand2.haveBrackets = true
            }
            stack.push(StackData("$operand2 $elem $operand1", maxPriority))
        }

        else {
            stack.push(StackData(elem, 0))
        }
        i++
    }

    return stack.poll()
}






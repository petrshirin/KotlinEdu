import java.io.File
import java.util.*
import kotlin.collections.ArrayList

val COW_OPERANDS: ArrayList<String> = arrayListOf(
        "moo",
        "mOo",
        "moO",
        "mOO",
        "Moo",
        "MOo",
        "MoO",
        "MOO",
        "OOO",
        "MMM",
        "OOM",
        "oom"
)


fun main(args: Array<String>) {
    val fileText = getFileText(args)
    var operands: ArrayList<String>? = null
    if (fileText != null) {
        operands = splitToOperands(fileText)
    }
    val interpreter = CowInterpreter()
    if (operands != null) {
        interpreter.start(operands)
    }

}


fun getFileText(args: Array<String>): String? {
    val filePath = args[0]
    return try {
        val file = File(filePath)
        val fileText = file.readText()
        fileText
    } catch (e: Exception) {
        println(e)
        null
    }
}


fun splitToOperands(fileText: String): ArrayList<String>? {
    val lines = fileText.split("\n")
    val operands: ArrayList<String> = arrayListOf()
    for (line in lines) {
        operands.addAll(line.split(" "))
    }
    return operands
}


class CowInterpreter {
    private var pull: Array<Int> = Array(10000) { 0 }
    private var currentCell: Int = 0
    private var currentConstruction: String? = null
    private var operandsLoop: ArrayList<String> = ArrayList()
    private var loopList: ArrayList<ArrayList<String>> = arrayListOf(ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())
    private var register: Int = 0
    private var currentLoop = 0

    fun start(operands: ArrayList<String>?) {
        if (operands != null) {
            for (operand in operands) {
                if (checkOperand(operand)) {
                    processOperand(operand)
                }
            }
        }

    }

    private fun checkOperand(operand: String): Boolean {
        if (operand in COW_OPERANDS) {
            return true
        }
        return false
    }

    private fun processOperand(operand: String) {
        if (currentConstruction == "loop") {
            if (operand == "moo") {
                loopList[currentLoop].plusAssign(operandsLoop)
                currentLoop -= 1
                operandsLoop = arrayListOf()
                if (currentLoop == -1) {
                    currentLoop = 0
                    processLoop(0)
                }
            }
            else if (operand == "MOO") {
                if (currentConstruction == "loop") {
                    operandsLoop.add("nextLoop")
                    loopList[currentLoop].plusAssign(operandsLoop)
                    operandsLoop = arrayListOf()
                    currentLoop += 1
                }
            }
            else {
                operandsLoop.add(operand)
            }
        }

        else if (operand == "MoO") {
            pull[currentCell] += 1
        }
        else if (operand == "MOo") {
            pull[currentCell] -= 1
        }
        else if (operand == "moO") {
            if (!changeCurrentCell(1)) {
                println("Exception: Overflow move")
            }
        }
        else if (operand == "mOo") {
            if (!changeCurrentCell(-1)) {
                println("Exception: Overflow move")
            }
        }
        else if (operand == "MOO") {
            currentConstruction = "loop"
        }
        else if (operand == "OOM") {
            print(pull[currentCell])
        }
        else if (operand == "oom") {
            var inputInt: Int? = null
            while (inputInt == null) {
                inputInt = processInput()
            }
            pull[currentCell] = inputInt
        }
        else if (operand == "Moo") {
            if (pull[currentCell] == 0) {
                var inputInt: Int? = null
                while (inputInt == null) {
                    inputInt = processInput()
                }
                pull[currentCell] = inputInt
            }
            else {
                print(pull[currentCell].toChar())
            }

        }
        else if (operand == "OOO") {
            pull[currentCell] = 0
        }
        else if (operand == "mOO") {
            if (pull[currentCell] != 3)
                processOperand(COW_OPERANDS[pull[currentCell]])
        }
        else if (operand == "MMM") {
            if (register != 0) {
                register = pull[currentCell]
            }
            else {
                pull[currentCell] = register
                register = 0
            }
        }
    }

    private fun changeCurrentCell(direction: Int): Boolean {
        if ((currentCell + direction < 1000) && (currentCell + direction >= 0)) {
            currentCell += direction
            return true
        }
        return false
    }

    private fun processLoop(loopNum: Int) {
        currentConstruction = null
        while (pull[currentCell] > 0) {
            for (operand in loopList[loopNum]) {
                if (operand == "nextLoop") {
                    processLoop(loopNum+1)
                }
                else
                    processOperand(operand)
            }
        }




    }

    private fun processInput(): Int? {
        val input = Scanner(System.`in`)
        return try {
            input.nextLine()[0].toByte().toInt()
        }
        catch (e: Exception) {
            null
        }

    }

}



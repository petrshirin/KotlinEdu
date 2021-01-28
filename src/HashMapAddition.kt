class SpecialHashMap() : HashMap<String, Int>() {
    public val iloc: ArrayList<Int> = ArrayList()
    override fun put(key: String, value: Int): Int? {
        val result = super.put(key, value)
        iloc.clear()

        val _keys = keys.toSortedSet(Comparator<String> { s1, s2 ->
            s1.compareTo(s2)
        })
        for (_key in _keys) {
            this[_key]?.let { iloc.add(it) }
        }
        return result
    }

    public val ploc: Ploc = Ploc()

    inner class Ploc() {
        operator fun get(conditions: String): Map<String, Int> {
            val condRegex = "[<>=]{1,2}[0-9]+".toRegex()
            val conditionsArr = condRegex.findAll(conditions)
                    .map { strToCondition(it.value) }.toList()
            val result: MutableMap<String, Int> = mutableMapOf()
            for (key in keys) {
                var _key = key.strip()
                if (!_key.startsWith('(') || !(_key.endsWith(')'))) {
                    if (conditionsArr.size != 1) continue
                    val isCorrect = _key.toIntOrNull()?.let { conditionsArr.first().checkAgainst(it) } ?: continue
                    if (isCorrect) {
                        this@SpecialHashMap[key]?.let { result.put(key, it) }
                    }
                    continue
                }
                _key = _key.drop(1).dropLast(1)
                val splitValues = _key.split(",")
                        .map { it.strip().toIntOrNull() }
                if (splitValues.contains(null)) {
                    continue
                }
                if (splitValues.size != conditionsArr.size) continue

                val isCorrect = splitValues.zip(conditionsArr).all { (value, condition) -> condition.checkAgainst(value!!) }
                if (isCorrect) {
                    this@SpecialHashMap[key]?.let { result.put(key, it) }
                }
            }
            return result

        }

        private fun strToCondition(strCondition: String): Condition {
            return when {
                strCondition.startsWith(">=") -> Condition(
                        strCondition.drop(2).toInt(), ConditionType.MORE_EQUALS)
                strCondition.startsWith("<=") -> Condition(
                        strCondition.drop(2).toInt(), ConditionType.LESS_EQUALS)
                strCondition.startsWith("<>") -> Condition(
                        strCondition.drop(2).toInt(), ConditionType.NOT_EQUALS)
                strCondition.startsWith("=") -> Condition(
                        strCondition.drop(1).toInt(), ConditionType.EQUALS)
                strCondition.startsWith(">") -> Condition(
                        strCondition.drop(1).toInt(), ConditionType.MORE)
                strCondition.startsWith("<") -> Condition(
                        strCondition.drop(1).toInt(), ConditionType.LESS)
                else -> error("GG WP")
            }

        }
    }

    private enum class ConditionType() {
        EQUALS,
        NOT_EQUALS,
        MORE,
        LESS,
        MORE_EQUALS,
        LESS_EQUALS,
    }

    private data class Condition(val arg: Int, val type: ConditionType) {

        fun checkAgainst(value: Int) = when (type) {
            ConditionType.EQUALS -> arg == value
            ConditionType.NOT_EQUALS -> arg != value
            ConditionType.MORE -> arg < value
            ConditionType.LESS -> arg > value
            ConditionType.MORE_EQUALS -> arg <= value
            ConditionType.LESS_EQUALS -> arg >= value
        }
    }
}


fun main(args: Array<String>) {
    val map = SpecialHashMap()
    map["value1"] = 1
    map["value2"] = 2
    map["value3"] = 3
    map["1"] = 10
    map["2"] = 20
    map["3"] = 30
    map["(1, 5)"] = 100
    map["(5, 5)"] = 200
    map["(10, 5)"] = 300
    map["(1, 5, 3)"] = 400
    map["(5, 5, 4)"] = 500
    map["(10, 5, 5)"] = 600

    println(map.ploc[">=1"]) // >>> {1=10, 2=20, 3=30}
    println(map.ploc["<3"]) // >>> {1=10, 2=20}

    println(map.ploc[">0, >0"]) // >>> {(1, 5)=100, (5, 5)=200, (10, 5)=300}
    println(map.ploc[">=10, >0"]) // >>> {(10, 5)=300}

    println(map.ploc["<5, >=5, >=3"]) // >>> {(1, 5, 3)=400}\\
}



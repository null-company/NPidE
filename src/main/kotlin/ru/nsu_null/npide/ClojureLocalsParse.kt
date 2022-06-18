package ru.nsu_null.npide

fun parseLocals(str: String): Map<String, String> {
    val stringRegex = """"([^"\\]|\\.)*"""".toRegex()
    val strWithoutStrings = stringRegex.replace(str) {
        "#".repeat(it.value.length)
    }
    var counter = 0
    var prevIdx = -1
    val vars = mutableListOf<IntRange>()
    for (i in str.indices) {
        if (str[i] in "{([") {
            counter++
            continue
        } else if (str[i] in "})]") {
            counter--
        }
        if (counter <= 1) {
            if (prevIdx == -1) {
                prevIdx = i
                continue
            }
            if (str[i] in ",}" || i == str.length - 1) {
                if (counter == 0) {
                    vars.add(prevIdx..i)
                } else {
                    vars.add(prevIdx..i + 1)
                }
                prevIdx = -1
            }
        }
    }
    val stringVarsTrimmed = vars.map { variableRange -> str.substring(variableRange).trim { it in ", \n" } }
    val nameToValue = mutableMapOf<String, String>()
    for (varStr in stringVarsTrimmed) {
        for (i in varStr.indices) {
            if (varStr[i] in "\n\r\t ") {
                nameToValue[varStr.slice(0 until i).trim { it in " \n" }] =
                    varStr.slice(i + 1 until varStr.length).trim { it in ", \n" }
                break
            }
        }
    }
    return nameToValue
}

fun main() {
    val vars = parseLocals(
        """{graph
 {:forward
  {"A" {"B" {:price 200, :tickets #<Ref@dfb38f: 5>}},
   "B" {"C" {:price 100, :tickets #<Ref@2be77417: 0>}}},
  :backward
  {"B" {"A" {:price 200, :tickets #<Ref@dfb38f: 5>}},
   "C" {"B" {:price 100, :tickets #<Ref@2be77417: 0>}}}},
 g
 {"A" {"B" {:price 200, :tickets #<Ref@dfb38f: 5>}},
  "B" {"C" {:price 100, :tickets #<Ref@2be77417: 0>}}}}
"""
    )
    for ((key, value) in vars) {
        println("$key = $value")
        println("")
    }
}

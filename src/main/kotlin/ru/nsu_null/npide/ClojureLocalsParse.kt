package ru.nsu_null.npide

import java.util.*
import kotlin.collections.HashMap

fun parseLocals(str: String): HashMap<String, String> {
    val string_literal = "\"([^\"\\\\]|\\\\.)*\""
    val string_regex = Regex(string_literal)
    val str_without_strings = string_regex.replace(str) {
        "#".repeat(it.value.length)
    }
    val getSubstring = { s: String, range: IntRange -> s.substring(range.start, range.endInclusive + 1) }
    var counter = 0
    var prev_idx = -1
    var vars = LinkedList<IntRange>();
    for (i in str.indices) {
        if (str[i] in "{([") {
            counter++;
            continue;
        } else if (str[i] in "})]") {
            counter--;
        }
        if (counter <= 1) {
            if (prev_idx == -1) {
                prev_idx = i;
                continue;
            }
            if (str[i] in ",}" || i == str.length - 1) {
                if(counter == 0){
                    vars.add(IntRange(prev_idx, i-1))
                }else{
                    vars.add(IntRange(prev_idx, i))
                }
                prev_idx = -1;
            }
        }
    }
    val stringVarsTrimmed = vars.map { getSubstring(str, it).trim { it in ", \n" } }
    val nameToValue = HashMap<String, String>()
    for (varStr in stringVarsTrimmed) {
        for (i in varStr.indices) {
            if (varStr[i] in "\n\r\t ") {
                nameToValue[varStr.slice(IntRange(0, i - 1)).trim { it in " \n" }] =
                    varStr.slice(IntRange(i + 1, varStr.length - 1)).trim { it in ", \n" }
                break
            }
        }
    }
    return nameToValue
}

fun main() {
    val vars = parseLocals(
        "{graph\n" +
                " {:forward\n" +
                "  {\"A\" {\"B\" {:price 200, :tickets #<Ref@dfb38f: 5>}},\n" +
                "   \"B\" {\"C\" {:price 100, :tickets #<Ref@2be77417: 0>}}},\n" +
                "  :backward\n" +
                "  {\"B\" {\"A\" {:price 200, :tickets #<Ref@dfb38f: 5>}},\n" +
                "   \"C\" {\"B\" {:price 100, :tickets #<Ref@2be77417: 0>}}}},\n" +
                " g\n" +
                " {\"A\" {\"B\" {:price 200, :tickets #<Ref@dfb38f: 5>}},\n" +
                "  \"B\" {\"C\" {:price 100, :tickets #<Ref@2be77417: 0>}}}}\n"
    )
    for ((key, value) in vars) {
        println("$key = $value")
        println("")
    }
}
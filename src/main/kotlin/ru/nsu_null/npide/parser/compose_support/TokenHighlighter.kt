package ru.nsu_null.npide.parser.compose_support

import com.google.gson.JsonParser

class TokenHighlighter(jsonHighlightRulesString: String, var defColor: String = "#000000") {
    private var hashMap = HashMap<String, String>()

    init {
        val json = JsonParser.parseString(jsonHighlightRulesString)
        val rules = json.asJsonObject.get("rules").asJsonArray
        for (rule in rules) {
            for (command in rule.asJsonObject.get("instructions").asJsonArray) {
                hashMap[command.asString] = rule.asJsonObject.get("color").asString
            }
        }
    }

    fun getColor(tokenValue: String): String {
        if (hashMap.contains(tokenValue)) {
            return hashMap[tokenValue]!!
        }
        return "#000000"
    }
}
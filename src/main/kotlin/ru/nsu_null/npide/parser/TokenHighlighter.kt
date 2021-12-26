import com.google.gson.*
import java.util.*

class TokenHighlighter(jsonHighlightRulesString: String, var defColor: String = "#000000") {
    var hashMap = HashMap<String, String>()

    init {
        val json = JsonParser.parseString(jsonHighlightRulesString)
        val rules = json.asJsonObject.get("rules").asJsonArray
        for (rule in rules) {
            for (command in rule.asJsonObject.get("instructions").asJsonArray) {
                hashMap.set(command.asString, rule.asJsonObject.get("color").asString)
            }
        }
    }

    fun getColor(tokenValue: String): String {
        if (hashMap.contains(tokenValue)) {
            return hashMap.get(tokenValue)!!;
        }
        return "#000000";
    }
}
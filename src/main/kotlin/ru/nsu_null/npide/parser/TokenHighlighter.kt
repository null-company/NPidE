import com.google.gson.*
import file_representation.FilePositionSplitter
import file_representation.Node
import java.util.*

class TokenHighlighter(jsonHighlightRulesString: String, var defColor: String = "#000000") {
    var hashMap = HashMap<String, String>()
    var openScopeRules: Vector<String> = Vector();
    var closeScopeRules: Vector<String> = Vector();

    // This variables describes which rules are for defining and usage variable
    var definitionRules: Vector<String> = Vector();
    var usageRules: Vector<String> = Vector();

    init {
        val json = JsonParser.parseString(jsonHighlightRulesString)
        val rules = json.asJsonObject.get("rules").asJsonArray
        for (rule in rules) {
            for (command in rule.asJsonObject.get("instructions").asJsonArray) {
                hashMap.set(command.asString, rule.asJsonObject.get("color").asString)
            }
        }
    }

    fun getColor(rule: String): String {
        if (hashMap.contains(rule)) {
            return hashMap.get(rule)!!;
        }
        return "#000000";
    }
}
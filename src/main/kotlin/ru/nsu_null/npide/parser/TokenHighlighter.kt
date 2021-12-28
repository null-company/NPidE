package ru.nsu_null.npide.parser

import com.google.gson.JsonParser
import ru.nsu_null.npide.parser.file_representation.FilePositionSplitter
import ru.nsu_null.npide.parser.file_representation.Node
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

    fun highlightTokens(file: FilePositionSplitter<String>): FilePositionSplitter<String> {
        val highlightedFile = FilePositionSplitter<String>();
        for (lineNum in 0..highlightedFile.lines.size) {
            val line = highlightedFile[lineNum]
            for (node in line.nodes) {
                highlightedFile[lineNum].nodes.add(
                    Node(
                        node.from,
                        node.to,
                        getColor(node.value)
                    )
                )
            }
        }
        return highlightedFile;
    }
}
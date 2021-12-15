//import ru.nsu_null.npide.parser.file_representation.FilePositionSplitter
//import ru.nsu_null.npide.parser.file_representation.Position
//import java.awt.Color
//
//fun colorToHex(color: Color): String {
//    var red = Integer.toHexString(color.red)
//    var green = Integer.toHexString(color.green)
//    var blue = Integer.toHexString(color.blue)
//
//    if (red.length == 1) red = "0$red"
//    if (green.length == 1) green = "0$green"
//    if (blue.length == 1) blue = "0$blue"
//
//    val hexColor = "#$red$green$blue"
//    return hexColor;
//}
//
//fun convertToHtml(lines: ArrayList<String>, textAnalyzer: TextAnalyzer): String {
//    val colors: FilePositionSplitter<String> = textAnalyzer.tokenizeText()
//    val build = StringBuilder()
//    build.append("<pre>")
//    for (i in 0 until lines.size) {
//        val line = StringBuilder(lines[i] + '\n')
//        var added = 0
//        for (color in colors[i].nodes) {
//            val pre_message =
//                "<span style=\"color :${color.value}\" position=\"${
//                    textAnalyzer.goToDefinition(
//                        Position(
//                            i,
//                            color.from
//                        )
//                    )
//                }\">"
//            val post_message = "</span>"
//            line.insert(color.from + added, pre_message)
//            added += pre_message.length
//            line.insert(color.to + added, post_message)
//            added += post_message.length
//        }
//        build.append(line)
//    }
//    build.append("</pre>")
//    return build.toString()
//}
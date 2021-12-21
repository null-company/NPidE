package file_representation

import me.tomassetti.kolasu.model.Node
import java.awt.Color

enum class NodeType {
    DECLARATION,
    USAGE,
    OTHER
}

open class Node<ValueT>(
    var from: Int,
    var to: Int,
    var value: ValueT
) {
    var type: NodeType = NodeType.OTHER;
    var definitionPosition = -1;
}


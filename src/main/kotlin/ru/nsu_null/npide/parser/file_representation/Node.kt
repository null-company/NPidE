package file_representation

import java.awt.Color

enum class NodeType {
    DECLARATION,
    USAGE,
    OTHER
}

class Node<ValueT>(
    var from: Int,
    var to: Int,
    var value: ValueT,
) {
    var type: NodeType = NodeType.OTHER;
    var definitionPosition: Position = Position(-1, -1);
}


package ru.nsu_null.npide.parser.file_representation

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
    var type: NodeType = NodeType.OTHER
    var definitionPosition = -1
}


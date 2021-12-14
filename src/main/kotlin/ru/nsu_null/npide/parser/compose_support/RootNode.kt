package ru.nsu_null.npide.parser.compose_support

import file_representation.Node
import me.tomassetti.kolasu.model.Point
import me.tomassetti.kolasu.model.Position

class RootNode<T>(
    from: Int,
    to: Int,
    val line: Int,
    value: T
) : Node<T>(from, to, value), me.tomassetti.kolasu.model.Node {
    override val position: Position
        get() = Position(Point(line + 1, from), Point(line + 1, to))
}
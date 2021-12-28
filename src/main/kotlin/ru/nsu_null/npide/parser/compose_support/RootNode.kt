package ru.nsu_null.npide.parser.compose_support

import me.tomassetti.kolasu.model.Point
import me.tomassetti.kolasu.model.Position
import ru.nsu_null.npide.parser.file_representation.Node

class RootNode<T>(
    from: Int,
    to: Int,
    val line: Int,
    value: T
) : Node<T>(from, to, value), me.tomassetti.kolasu.model.Node {
    override val position: Position
        get() = Position(Point(line + 1, from), Point(line + 1, to))
}
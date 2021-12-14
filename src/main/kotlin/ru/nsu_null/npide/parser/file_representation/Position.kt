package ru.nsu_null.npide.parser.file_representation

class Position(
    var line: Int,
    var lineOffset: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (line != other.line) return false
        if (lineOffset != other.lineOffset) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line
        result = 31 * result + lineOffset
        return result
    }

    override fun toString(): String {
        return "Position(line=$line, lineOffset=$lineOffset)"
    }

}

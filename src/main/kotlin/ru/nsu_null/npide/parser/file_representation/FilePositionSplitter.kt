package ru.nsu_null.npide.parser.file_representation

class FilePositionSplitter<T> {
    var lines: ArrayList<Line<T>> = ArrayList()
    operator fun get(index: Int): Line<T> {
        while (lines.size <= index) {
            lines.add(Line(ArrayList()))
        }
        return lines[index]
    }
}
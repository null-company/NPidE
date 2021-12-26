package ru.nsu_null.npide.parser.translation

class SymbolTable {
    private val map = HashMap<String, Int>();
    fun getSymbolPos(symbolName: String): Int {
        return map.get(symbolName)!!;
    }

    fun setSymbol(symbolName: String, pos: Int) {
        map[symbolName] = pos;
    }

    fun contains(symbolName: String): Boolean {
        return map.containsKey(symbolName);
    }
}
package ru.nsu_null.npide.ui.console

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// that could be an object to achieve singleton,
// didn't do that for monotony
class Console {
    var content: MutableState<String> = mutableStateOf("")
        private set

    fun add(newContent: String) {
        content += newContent
        try {
            if (newContent.last() == '\n') {
                content += "\n"
            }
        } catch (e: NoSuchElementException) {
        }
    }
}

private operator fun MutableState<String>.plusAssign(other: String) {
    this.value += other
}

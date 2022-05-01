package ru.nsu_null.npide.ui.console

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.io.PipedOutputStream

class Console {
    var content: MutableState<String> = mutableStateOf("")
        private set

    /**
     * Display new content in console
     */
    fun display(newContent: String) {
        content += newContent
    }

    private var userInputBuffer: String = ""
    private var userInput = PipedOutputStream()

    /**
     * Read from buffer that the user is writing to
     */
    fun read(): String = userInputBuffer.also { userInputBuffer = "" }

    /**
     * Writes that are performed by user
     */
    fun write(newUserContent: String) {
        userInputBuffer += newUserContent
    }
}

private operator fun MutableState<String>.plusAssign(other: String) {
    this.value += other
}

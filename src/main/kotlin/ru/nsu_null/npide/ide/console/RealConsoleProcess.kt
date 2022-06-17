package ru.nsu_null.npide.ide.console

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Represents a real process (as in a process in OS terms)
 * as a [ConsoleProcess]
 */
class RealConsoleProcess (
    private val process: Process
): ConsoleProcess {

    override val outputStream: OutputStream = BufferedOutputStream(process.outputStream)

    override val inputStream: InputStream = BufferedInputStream(process.inputStream)

    override val errorStream: InputStream = BufferedInputStream(process.errorStream)

    override val isAlive: Boolean
        get() = process.isAlive

    override fun destroy() {
        process.destroy()
    }

    override fun exitValue(): Int {
        return process.exitValue();
    }
}
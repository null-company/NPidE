package ru.nsu_null.npide.ide.console.process

import ru.nsu_null.npide.ide.util.MethodDelegator
import java.io.InputStream
import java.io.OutputStream

/**
 * Represents a real process (as in a process in OS terms)
 * as a [ConsoleProcess]
 */
class RealConsoleProcess (
    private val process: Process
): ConsoleProcess {

    override val outputStream: OutputStream by process::outputStream

    override val inputStream: InputStream by process::inputStream

    override val errorStream: InputStream by process::errorStream

    override val isAlive: Boolean by MethodDelegator(process::isAlive)

    override fun destroy() = process.destroy()

    override val exitValue: Int by MethodDelegator(process::exitValue)
}

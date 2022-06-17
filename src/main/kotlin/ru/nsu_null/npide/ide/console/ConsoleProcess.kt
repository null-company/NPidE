package ru.nsu_null.npide.ide.console

import java.io.InputStream
import java.io.OutputStream

/**
 *  Represents an entity that can be attached to [Console]
 */
interface ConsoleProcess {

    /**
     * An [InputStream] connected to the standard output of the process
     */
    val outputStream: InputStream

    /**
     * An [OutputStream] connected to the standard input of the process
     */
    val inputStream: OutputStream

    /**
     * An [InputStream] connected to the error output of the process
     */
    val errorStream: InputStream

    /**
     * Kills the console process
     *
     * Any action on the streams is undefined after this method is called
     */
    fun destroy()
}
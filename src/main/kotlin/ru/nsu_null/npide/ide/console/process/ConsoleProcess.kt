package ru.nsu_null.npide.ide.console.process

import java.io.InputStream
import java.io.OutputStream

/**
 *  Represents an entity that can be attached to [Console]
 */
interface ConsoleProcess {

    // Explanation on why the iostreams are named confusingly:
    // the java.lang.Process class has the same semantics of them
    // so the semantics of them remain so
    // This is done so that java.lang.Process can be kinda interchangeable with this interface

    /**
     * An [OutputStream] connected to the standard input of the process
     */
    val outputStream: OutputStream

    /**
     * An [InputStream] connected to the standard output of the process
     */
    val inputStream: InputStream

    /**
     * An [InputStream] connected to the error output of the process
     */
    val errorStream: InputStream

    /**
     * Is the process still alive?
     */
    val isAlive: Boolean

    /**
     * Kills the console process
     *
     * Any action on the streams is undefined after this method is called
     */
    fun destroy()

    /**
     * Returns the exit code of the process
     *
     * Exit code 0 means the execution was successful
     */
    fun exitValue(): Int
}
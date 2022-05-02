package ru.nsu_null.npide.ui.console

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.nsu_null.npide.ui.npide.NPIDE
import java.io.*

class Console {
    var content: String by mutableStateOf("")
        private set

    /**
     * A simple way to show new text content in console
     * Doesn't get sent to any process
     */
    fun display(newContent: String) {
        content += newContent
    }

    fun clear() {
        content = ""
    }

    /**
     * Write a string to be sent to an attached process if such process exists
     *
     * Calls [display] in both situations
     */
    fun send(command: String) {
        display(command)
        if (!processIsAttached) {
            return
        }
        val process = attachedProcess!!
        val outputStream = process.outputStream
        // the writer is not closed on purpose so that it is possible to write to it later
        outputStream.writer().write(command)
    }

    private var attachedProcess: Process? by mutableStateOf(null)
    var attachedProcessLabel: String? = null
        get() = if (!processIsAttached) null else field
        private set
    var processIsAttached: Boolean = false
        get() = attachedProcess != null
        private set

    private var middleWareWorker: MiddleWareWorker? = null

    /**
     * Attach a process to console for user and ide-wide communication
     * Allows user to write to the process and see the output in the console
     * Allows communicating with the process programmatically using ProcessCommunicationPipes
     * User sees programmatic communication in the console
     * @param process the process to attach to console
     * @param label some name for this process. The user will see it in the gui
     * @return pipes with which to communicate with the process programmatically
     */
    fun attachProcess(process: Process, label: String): ProcessCommunicationPipes {
        log("Attaching process $label")
        if (processIsAttached) {
            detachCurrentProcess()
        }
        attachedProcess = process
        attachedProcessLabel = label

        val processStdout = PipedInputStream()
        val processStdin = PipedOutputStream()

        val workerInput = PipedInputStream().also { it.connect(processStdin) }
        val workerOutput = PipedOutputStream().also { it.connect(processStdout) }

        middleWareWorker = MiddleWareWorker(
            ProcessCommunicationPipes(workerOutput, workerInput),
            ProcessStreams(process.outputStream, process.inputStream)
        ).also { it.work() }

        return ProcessCommunicationPipes(processStdin, processStdout)
    }

    /**
     * Detaches current process from console, if such process exists
     */
    fun detachCurrentProcess() {
        val process = attachedProcess
        if (process == null) {
            log("Error: no attached process")
        } else {
            log("Detaching process $attachedProcessLabel")
            process.destroy()
            middleWareWorker?.threads?.forEach(Thread::interrupt)
            middleWareWorker = null
            attachedProcess = null
        }
    }

    private fun log(message: String) {
        log("Console", message)
    }
}

data class ProcessCommunicationPipes(val to: PipedOutputStream, val from: PipedInputStream)
private data class ProcessStreams(val stdin: OutputStream, val stdout: InputStream)

private class MiddleWareWorker(clientPipes: ProcessCommunicationPipes,
                               processStreams: ProcessStreams) {
    var stdinWriter: OutputStreamWriter? = processStreams.stdin.writer()
    var stdoutReader: InputStreamReader? = processStreams.stdout.reader()
    var clientReader: InputStreamReader? = clientPipes.from.reader()
    var clientWriter: OutputStreamWriter? = clientPipes.to.writer()

    private fun safeThreadLaunch(task: () -> Unit) = Thread {
        while (true) {
            try {
                if (Thread.interrupted()) {
                    throw InterruptedException()
                }
                task()
            } catch (e: InterruptedException) {
                return@Thread
            } catch (e: IOException) {
                return@Thread
            }
        }
    }
    val threads = listOf(safeThreadLaunch(::communicateClientWithStdin),
        safeThreadLaunch(::communicateStdoutWithConsoleAndClient))
    fun work() {
        threads.forEach(Thread::start)
    }
    private fun InputStreamReader.readSafe(): Int? = read().let { if (it != -1) it else null }
    private fun communicateClientWithStdin() {
        val clientInput = clientReader ?: return
        val clientMessage = try { clientInput.readSafe() } catch (e: IOException) { null }
        if (clientMessage != null) {
            stdinWriter?.write(clientMessage)
        } else {
            stdinWriter?.close()
            clientInput.close()
            stdinWriter = null
            clientReader = null
        }
    }
    private fun communicateStdoutWithConsoleAndClient() {
        val stdout = stdoutReader ?: return
        val stdoutMessage = try { stdout.readSafe() } catch (e: IOException) { null }
        if (stdoutMessage != null) {
            NPIDE.console.display(Char(stdoutMessage).toString())
            clientWriter?.write(stdoutMessage)
        } else {
            stdout.close()
            clientWriter?.close()
            stdoutReader = null
            clientWriter = null
        }
    }
}


fun Console.log(who: String, message: String) {
    display("[$who]: $message\n")
}

package ru.nsu_null.npide.ui.console

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.*

class Console {
    /**
     * Text in console that is currently available
     */
    var content: String by mutableStateOf("")
        private set

    /**
     * A simple way to show new text content in console
     * Doesn't get sent to any process
     */
    fun display(newContent: String) {
        content += newContent
    }

    /**
     * Delete all content from the console rendering it clear
     */
    fun clear() {
        content = ""
    }

    /**
     * Write a string to be sent to an attached process if such process exists
     * [display] method is not called, but it could be okay because child process does that anyway
     */
    fun send(command: String) {
        if (!processIsAttached) {
            display(command)
            return
        }
        val process = attachedProcess!!
        val outputStream = process.outputStream
        outputStream.write(command.encodeToByteArray())
        outputStream.flush()
    }

    private var attachedProcess: Process? by mutableStateOf(null)

    /**
     * Label of the attached process, if such process exists, null otherwise
     */
    var attachedProcessLabel: String? = null
        get() = if (!processIsAttached) null else field
        private set

    var processIsAttached: Boolean = false
        get() = attachedProcess != null
        private set

    private var communicationProxyWorker: CommunicationProxyWorker? = null

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

        val processStdin = PipedOutputStream()
        val processStdout = PipedInputStream()
        val processStderr = PipedInputStream()

        val workerStdin = PipedInputStream().also { it.connect(processStdin) }
        val workerStdout = PipedOutputStream().also { it.connect(processStdout) }
        val workerStderr = PipedOutputStream().also { it.connect(processStderr) }

        communicationProxyWorker = CommunicationProxyWorker(
            this,
            ConsoleProxyPipes(workerStdin, workerStdout, workerStderr),
            ProcessCommunicationPipes(process.outputStream, process.inputStream, process.errorStream)
        ).also { it.work() }

        return ProcessCommunicationPipes(processStdin, processStdout, processStderr)
    }

    /**
     * Detaches current process from console, if such process exists
     *
     * Calls [Process.destroy] on current process
     */
    fun detachCurrentProcess() {
        val process = attachedProcess
        if (process == null) {
            log("Error: no attached process")
        } else {
            log("Detaching process $attachedProcessLabel")
            process.destroy()
            communicationProxyWorker?.stop()
            communicationProxyWorker = null
            attachedProcess = null
        }
    }

    private fun log(message: String) {
        log("Console", message)
    }
}

data class ProcessCommunicationPipes(val stdin: OutputStream,
                                     val stdout: InputStream,
                                     val stderr: InputStream)

private data class ConsoleProxyPipes(val stdin: InputStream,
                                     val stdout: OutputStream,
                                     val stderr: OutputStream)

/**
 * [CommunicationProxyWorker] is responsible for proxying client's pipes by writing
 * the messages to console
 *
 * A client is a caller of [Console.attachProcess]
 * @param console console to which to print
 * @param clientPipes streams with which communication with client happens
 * @param processStreams streams with which communication with the process happens
 */
private class CommunicationProxyWorker(val console: Console,
                                       clientPipes: ConsoleProxyPipes,
                                       processStreams: ProcessCommunicationPipes) {
    var stdin: OutputStreamWriter? = processStreams.stdin.writer()
    var stdout: InputStreamReader? = processStreams.stdout.reader()
    var stderr: InputStreamReader? = processStreams.stderr.reader()

    var clientStdin: InputStreamReader? = clientPipes.stdin.reader()
    var clientStdout: OutputStreamWriter? = clientPipes.stdout.writer()
    var clientStderr: OutputStreamWriter? = clientPipes.stderr.writer()

    fun work() {
        safeThreadContinuousTask(::communicate).start()
    }

    private fun communicate() {
        // todo ineffective to construct this every time?
        val communicatingPairs = listOf(
            clientStdin to stdin,
            stdout to clientStdout,
            stderr to clientStderr
        ).mapNotNull { (l, r) ->  if (l == null || r == null) null else l to r }
        communicatingPairs.forEach { (input, output) -> communicatePipes(input, output) }
    }

    private fun communicatePipes(inputStreamReader: InputStreamReader, outputStreamWriter: OutputStreamWriter: OutputStreamWriter) {
        val readAndAccumulate = {
            while (inputStreamReader.ready()) {
                val newChar = inputStreamReader.read()
                if (newChar == -1)
                    break
                acc += Char(newChar)
            }
        }
    }

    private fun InputStreamReader.readSafe(): Int? = read().let { if (it != -1) it else null }
    private fun communicateClientWithStdin() {
        val clientInput = clientStdin ?: return
        val clientMessage = try { clientInput.readSafe() } catch (e: IOException) { null }
        if (clientMessage != null) {
            stdin?.write(clientMessage)
            stdin?.flush()
        } else {
            stdin?.close()
            clientInput.close()
            stdin = null
            clientStdin = null
        }
    }
    private fun communicateStdoutWithConsoleAndClient() {
        val actualStdout = stdout ?: return
        val stdoutMessage = try { actualStdout.readSafe() } catch (e: IOException) { null }
        if (stdoutMessage != null) {
            console.display(Char(stdoutMessage).toString())
            clientStdout?.write(stdoutMessage)
            clientStdout?.flush()
        } else {
            actualStdout.close()
            clientStdout?.close()
            stdout = null
            clientStdout = null
        }
    }
    private fun communicateStderrWithConsoleAndClient() {
        val actualStderr = stderr ?: return
        val stderrMessage = try { actualStderr.readSafe() } catch (e: IOException) { null }
        if (stderrMessage != null) {
            console.display(Char(stderrMessage).toString())
            clientStderr?.write(stderrMessage)
            clientStderr?.flush()
        } else {
            actualStderr.close()
            clientStderr?.close()
            stderr = null
            clientStderr = null
        }
    }

    fun stop() {
        threads.forEach(Thread::interrupt)
    }
}

/**
 * Calls [Console.display] and [Console.send]
 *
 * Can be used as a shortcut when the child process doesn't echo written command,
 * but is the desired behaviour
 */
fun Console.displayAndSend(command: String) {
    display(command)
    send(command)
}

/**
 * Log to NPidE console
 * @param who sender name to be displayed
 * @param message text to log
 */
fun Console.log(who: String, message: String) {
    display("[$who]: $message\n")
}

private fun safeThreadContinuousTask(task: () -> Unit) = Thread {
    while (true) {
        try {
            if (Thread.interrupted()) {
                throw InterruptedException()
            }
            task()
        } catch (e: InterruptedException) {
            return@Thread
        } catch (e: IOException) {
            continue
        }
    }
}

/**
 * An example of [Console.attachProcess] usage
 */
private fun main() {

    val console = Console()

    // Create a process
    val gitStatusProcess = Runtime.getRuntime().exec(arrayOf("git", "status"))

    // Immediately attach it. If some sneaky-beaky like action is needed, perform it before attaching
    // But the user won't see any action in the console then
    val (stdin, stdout, stderr) = console.attachProcess(gitStatusProcess, "git status")

    // Since this moment, only use the given streams to interact with the process so that the users sees everything

    @Suppress("unused")
    fun doStuff() {
        stdin.write("Hello, git!".toByteArray())
        stdout.reader().use { println(it.readText()) }
    }

    // Good manners
    listOf(stdin, stdout, stderr).forEach(Closeable::close)

    Thread.sleep(500) // waiting for the git process to tell everything it needs for presentation purposes

    // You MAY do that in the end. This kills the process
    console.detachCurrentProcess()

    println(console.content)
}

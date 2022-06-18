package ru.nsu_null.npide.ide.projectstrategies.defaults.delegators

import ru.nsu_null.npide.ide.console.Console
import ru.nsu_null.npide.ide.console.Logger
import ru.nsu_null.npide.ide.console.process.ConsoleProcess
import ru.nsu_null.npide.ide.console.process.RealConsoleProcess
import ru.nsu_null.npide.ide.npide.NPIDE
import ru.nsu_null.npide.ide.platform.toJavaFile
import ru.nsu_null.npide.ide.projectstrategies.*
import ru.nsu_null.npide.ide.storage.BreakPoints
import ru.nsu_null.npide.ide.storage.DirtyFlags
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicBoolean

private fun buildCommand(
    executableName: String,
    scriptToLaunch: String,
    mode: String, // build, run, debug
    projectName: String,
    projectRoot: String,
    projectFiles: List<String>,
    entryPoint: String,
    ext: String,
    breakPoints: BreakPoints
): List<String> {
    var breakPointsAsString = ""
    for (key in breakPoints.keys) {
        breakPointsAsString += key
        breakPointsAsString += ":: "
        for (bp in breakPoints[key]!!) {
            breakPointsAsString += bp
            breakPointsAsString += ", "
        }
        breakPointsAsString = breakPointsAsString.subSequence(0, breakPointsAsString.length - 2).toString()
        breakPointsAsString += "; "
    }
    breakPointsAsString =
        if (breakPointsAsString.isEmpty()) ""
        else breakPointsAsString.subSequence(0, breakPointsAsString.length - 2).toString()
    return listOf(
        executableName, scriptToLaunch,
        "-f", mode,
        "-n", projectName,
        "-d", projectRoot,
        "-p", projectFiles.joinToString(" "),
        "-e", entryPoint,
        "-ext", ext,
        (if (breakPointsAsString.isNotEmpty()) "-b" else ""), breakPointsAsString)
}

private fun runCommand(arguments: List<String>): Process {
    val argumentsArr = arguments.filter { it.trim().isNotEmpty() }.toTypedArray()

    val projectRoot = NPIDE.currentProject!!.rootFolder.toJavaFile()

    return Runtime.getRuntime().exec(argumentsArr, null, projectRoot)
}


private const val BUILD_COMMAND = "build"
private const val RUN_COMMAND = "run"
private const val DEBUG_COMMAND = "debug"

abstract class RealProcessBackedStrategy : ConsoleProcess {
    protected lateinit var workerProcess: RealConsoleProcess

    override val outputStream: OutputStream by workerProcess::outputStream
    override val inputStream: InputStream by workerProcess::inputStream
    override val errorStream: InputStream by workerProcess::errorStream
    override val isAlive: Boolean by workerProcess::isAlive
    override val exitValue: Int by workerProcess::exitValue
    override fun destroy() = workerProcess.destroy()
}

/**
 * Depends on:
 *  - an executable name on key "executable" to run and delegate to it
 *  - a script path, which will be executed by the executable, named "script"
 */
class RunnerDelegatorStrategy : RealProcessBackedStrategy(), RunnerStrategy {

    val name = "RunnerDelegatorStrategy"

    override fun run(strategyContext: ProjectStrategyContext,
                     extraConfiguration: ExtraConfiguration,
                     logger: Logger) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraConfiguration["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPath = extraConfiguration["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath,
                mode = RUN_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
                ext = "",
                breakPoints = mutableMapOf()
            )
            workerProcess = RealConsoleProcess(runCommand(command))
        } catch (e: IOException) {
            logError("During run, the following exception occurred: $e")
        }
    }
}

/**
 * Depends on:
 *  - an executable name on key "executable" to run and delegate to it
 *  - a script path, which will be executed by the executable, named "script"
 */
class BuilderDelegatorStrategy : RealProcessBackedStrategy(), BuilderStrategy {
    val name = "BuilderDelegatorStrategy"
    override fun build(
        enableDebugInfo: Boolean,
        strategyContext: ProjectStrategyContext,
        extraConfiguration: ExtraConfiguration,
        breakPoints: BreakPoints,
        dirtyFlags: DirtyFlags,
        logger: Logger
    ) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraConfiguration["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPath = extraConfiguration["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath,
                mode = BUILD_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
                ext = "",
                breakPoints = breakPoints
            )
            workerProcess = RealConsoleProcess(runCommand(command))
        } catch (e: IOException) {
            logError("During run, the following exception occurred: $e")
        }
    }
}

/**
 * Depends on:
 *  - an executable name on key "executable" to run and delegate to it
 *  - a script path, which will be executed by the executable, named "script"
 *  - step command, "step"
 *  - continue command, "continue"
 *  - watches command for map, "watches-map"
 *  - watches command for string, "watches-string"
 */
class DebuggerDelegatorStrategy : RealProcessBackedStrategy(), DebuggerStrategy {
    val name = "DebuggerDelegateStrategy"

    private lateinit var extraConfiguration: ExtraConfiguration
    private lateinit var strategyContext: ProjectStrategyContext
    private val debugRunnableStepFlag = AtomicBoolean(true)

    override fun debug(
        strategyContext: ProjectStrategyContext,
        extraConfiguration: ExtraConfiguration,
        breakPoints: BreakPoints,
        logger: Logger
    ) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraConfiguration["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPath = extraConfiguration["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        this.extraConfiguration = extraConfiguration
        this.strategyContext = strategyContext
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath,
                mode = DEBUG_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
                ext = "",
                breakPoints = breakPoints
            )
            workerProcess = RealConsoleProcess(debugRun(command))
        } catch (e: IOException) {
            logError("During run, the following exception occurred: $e")
        }
    }

    private fun debugRun(command: List<String>): Process {
        val projectRoot = File(strategyContext.projectRoot)

        return Runtime.getRuntime().exec(
            command.map { it.trim() }.filter { it.isNotEmpty() }.toTypedArray(),
            null,
            projectRoot
        )
    }

    override fun sendGeneralCommand(command: String) {
        workerProcess.outputStream.writer().write(command)
    }

    override fun step() {
        val stepMessage = extraConfiguration["step"]
            ?: throw IllegalArgumentException("extra configuration did not provide step command")
        workerProcess.outputStream.writer().write(stepMessage)
    }

    override fun cont() {
        val continueMessage = extraConfiguration["continue"]
            ?: throw IllegalArgumentException("extra configuration did not provide continue command")
        workerProcess.outputStream.writer().write(continueMessage)
    }

    override fun getWatches(): Map<String, String> {
        val watchesMapMessage = extraConfiguration["watches-map"]
            ?: throw IllegalArgumentException("extra configuration did not provide watches-map command")
        workerProcess.outputStream.writer().write(watchesMapMessage)
        return mapOf("unimplemented" to ":C") // todo
    }

    override fun getWatchesAsString(): String {
        val watchesStringMessage = extraConfiguration["watches-string"]
            ?: throw IllegalArgumentException("extra configuration did not provide watches-string command")
        workerProcess.outputStream.writer().write(watchesStringMessage)
        return "unimplemented" // todo
    }
}

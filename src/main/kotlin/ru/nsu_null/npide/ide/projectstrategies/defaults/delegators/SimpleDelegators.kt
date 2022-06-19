package ru.nsu_null.npide.ide.projectstrategies.defaults.delegators

import kotlinx.serialization.encodeToString
import ru.nsu_null.npide.ide.console.Console
import ru.nsu_null.npide.ide.console.Logger
import ru.nsu_null.npide.ide.console.process.ConsoleProcess
import ru.nsu_null.npide.ide.console.process.RealConsoleProcess
import ru.nsu_null.npide.ide.npide.NPIDE
import ru.nsu_null.npide.ide.platform.toJavaFile
import ru.nsu_null.npide.ide.projectstrategies.*
import ru.nsu_null.npide.ide.projectstrategies.DebuggerAbility.*
import ru.nsu_null.npide.ide.storage.BreakPoints
import ru.nsu_null.npide.ide.storage.DirtyFlags
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.div
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlinx.serialization.json.Json

private fun buildCommand(
    executableName: String,
    scriptToLaunch: String,
    mode: String, // build, run, debug
    projectName: String,
    projectRoot: String,
    projectFiles: List<String>,
    entryPoint: String,
    breakPoints: BreakPoints
): List<String> {
    return listOf(
        executableName, scriptToLaunch,
        "-f", mode,
        "-n", projectName,
        "-d", projectRoot,
        "-p", projectFiles.joinToString(" "),
        "-e", entryPoint,
        "-b", Json.encodeToString(breakPoints)
    )
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

    inner class LazyDelegate<T>(private val workerProperty: KProperty1<RealConsoleProcess, T>) {
        operator fun getValue(
            realProcessBackedStrategy: RealProcessBackedStrategy,
            property: KProperty<*>
        ): T {
            @Suppress("UNCHECKED_CAST")
            return workerProperty.get(workerProcess)
        }
    }

    override val outputStream: OutputStream by LazyDelegate(RealConsoleProcess::outputStream)
    override val inputStream: InputStream by LazyDelegate(RealConsoleProcess::inputStream)
    override val errorStream: InputStream by LazyDelegate(RealConsoleProcess::errorStream)
    override val isAlive: Boolean by LazyDelegate(RealConsoleProcess::isAlive)
    override val exitValue: Int by LazyDelegate(RealConsoleProcess::exitValue)
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
                     extraParameters: ExtraParameters,
                     logger: Logger) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraParameters["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPathRelative = extraParameters["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        val scriptPath =
            (Path.of(strategyContext.languageDistributionPath).parent / Path.of(scriptPathRelative)).toRealPath()
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath.absolutePathString(),
                mode = RUN_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
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
        extraParameters: ExtraParameters,
        breakPoints: BreakPoints,
        dirtyFlags: DirtyFlags,
        logger: Logger
    ) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraParameters["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPathRelative = extraParameters["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        val scriptPath =
            (Path.of(strategyContext.languageDistributionPath).parent / Path.of(scriptPathRelative)).toRealPath()
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath.absolutePathString(),
                mode = BUILD_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
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
 *  - watches command for string, "watches-string"
 */
class DebuggerDelegatorStrategy : RealProcessBackedStrategy(), DebuggerStrategy {

    // does not support WatchesMap
    override val abilities: Set<DebuggerAbility> = setOf(
        Step, WatchesString, Continue
    )

    val name = "DebuggerDelegateStrategy"

    private lateinit var extraParameters: ExtraParameters
    private lateinit var strategyContext: ProjectStrategyContext

    override fun debug(
        strategyContext: ProjectStrategyContext,
        extraParameters: ExtraParameters,
        breakPoints: BreakPoints,
        logger: Logger
    ) {
        fun logError(message: String) = logger.log(name, message, Console.MessageType.Error)
        val executableName = extraParameters["executable"]
            ?: throw IllegalArgumentException("extra configuration did not provide an executable")
        val scriptPathRelative = extraParameters["script"]
            ?: throw IllegalArgumentException("extra configuration did not provide a script to launch")
        val scriptPath =
            (Path.of(strategyContext.languageDistributionPath).parent / Path.of(scriptPathRelative)).toRealPath()
        this.extraParameters = extraParameters
        this.strategyContext = strategyContext
        try {
            val command = buildCommand(
                executableName = executableName,
                scriptToLaunch = scriptPath.absolutePathString(),
                mode = DEBUG_COMMAND,
                projectName = strategyContext.projectName,
                projectRoot = strategyContext.projectRoot,
                projectFiles = strategyContext.projectFiles,
                entryPoint = strategyContext.entryPoint,
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
        val stepMessage = extraParameters["step"]
            ?: throw IllegalArgumentException("extra configuration did not provide step command")
        workerProcess.outputStream.writer().write(stepMessage)
    }

    override fun cont() {
        val continueMessage = extraParameters["continue"]
            ?: throw IllegalArgumentException("extra configuration did not provide continue command")
        workerProcess.outputStream.writer().write(continueMessage)
    }

    override fun getWatches(): Map<String, String> {
        throw UnsupportedOperationException()
    }

    override fun getWatchesAsString(): String {
        val watchesStringMessage = extraParameters["watches-string"]
            ?: throw IllegalArgumentException("extra configuration did not provide watches-string command")
        workerProcess.outputStream.writer().write(watchesStringMessage)
        return "unimplemented" // todo
    }
}

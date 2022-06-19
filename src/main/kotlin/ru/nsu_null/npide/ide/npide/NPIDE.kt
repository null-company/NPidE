package ru.nsu_null.npide.ide.npide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.nsu_null.npide.ide.config.ConfigManager
import ru.nsu_null.npide.ide.console.Console
import ru.nsu_null.npide.ide.console.ConsoleLogger
import ru.nsu_null.npide.ide.console.log
import ru.nsu_null.npide.ide.console.runProcess
import ru.nsu_null.npide.ide.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ide.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ide.projectchooser.ProjectChooser.Project
import ru.nsu_null.npide.ide.projectstrategies.*
import ru.nsu_null.npide.ide.storage.ProjectStorage
import java.io.File
import java.net.URLClassLoader

object NPIDE {
    var state: State by mutableStateOf(CHOOSING_PROJECT)
        private set

    enum class State {
        CHOOSING_PROJECT,
        IN_PROJECT
    }

    var currentProject: Project? = null
    lateinit var configManager: ConfigManager
    lateinit var projectStorage: ProjectStorage

    lateinit var builder: BuilderStrategy
    lateinit var runner: RunnerStrategy
    lateinit var debugger: DebuggerStrategy

    lateinit var console: Console

    fun openProject(project: Project) {
        currentProject = project
        console = Console()
        configManager = ConfigManager(project)
        projectStorage = ProjectStorage(project)
        loadProjectWorkers()
        state = IN_PROJECT
    }

    private fun loadProjectWorkers() {
        val languageDistributionDir = File(configManager.currentProjectConfig.languageDistribution)
        builder = instantiateStrategy(
            "ru.nsu_null.npide.ide.projectstrategies.defaults.delegators.BuilderDelegatorStrategy",
            languageDistributionDir
        )
        runner = instantiateStrategy(
            "ru.nsu_null.npide.ide.projectstrategies.defaults.delegators.RunnerDelegatorStrategy",
            languageDistributionDir
        )
        debugger = instantiateStrategy(
            "ru.nsu_null.npide.ide.projectstrategies.defaults.delegators.DebuggerDelegatorStrategy",
            languageDistributionDir
        )
    }

    private inline fun <reified T> instantiateStrategy(className: String, searchDir: File): T {
        val cl = URLClassLoader(arrayOf(searchDir.toURI().toURL()))
        val strategyClass = cl.loadClass(className)
        return strategyClass.getConstructor().newInstance() as T
    }

    fun openChooseProject() {
        currentProject = null
        state = CHOOSING_PROJECT
    }

    fun buildCurrentProject(enableDebugInfo: Boolean) {
        val context = ProjectStrategyContext.fromProjectConfig(configManager.currentProjectConfig)
        val buildStrategyInfo = configManager.currentLanguageDistributionInfo.buildStrategy
        val breakPoints = projectStorage.breakpointStorage.breakPoints
        val dirtyFlags = projectStorage.dirtyFlagsStorage.dirtyFlags
        val consoleLogger = ConsoleLogger(console)
        try {
            builder.build(
                enableDebugInfo,
                context,
                buildStrategyInfo.extraParameters,
                breakPoints,
                dirtyFlags,
                consoleLogger
            )
            console.runProcess(builder, "build")
        } catch (e: Exception) {
            logError(e.stackTraceToString())
            e.printStackTrace()
        }
    }

    fun runCurrentProject() {
        val context = ProjectStrategyContext.fromProjectConfig(configManager.currentProjectConfig)
        val buildStrategyInfo = configManager.currentLanguageDistributionInfo.buildStrategy
        val consoleLogger = ConsoleLogger(console)
        try {
            runner.run(
                context,
                buildStrategyInfo.extraParameters,
                consoleLogger
            )
            console.runProcess(runner, "run")
        } catch (e: Exception) {
            logError(e.stackTraceToString())
            e.printStackTrace()
        }
    }

    fun debugCurrentProject() {
        val context = ProjectStrategyContext.fromProjectConfig(configManager.currentProjectConfig)
        val buildStrategyInfo = configManager.currentLanguageDistributionInfo.buildStrategy
        val breakPoints = projectStorage.breakpointStorage.breakPoints
        val consoleLogger = ConsoleLogger(console)
        try {
            debugger.debug(
                context,
                buildStrategyInfo.extraParameters,
                breakPoints,
                consoleLogger
            )
            console.runProcess(debugger, "debug")
        } catch (e: Exception) {
            logError(e.stackTraceToString())
            e.printStackTrace()
        }
    }

    fun debuggerStep() {
        if (DebuggerAbility.Step !in debugger.abilities) {
            throw IllegalStateException("impossible to call step, debugger can not do that")
        }
        try {
            debugger.step()
        } catch (e: Exception) {
            logError(e.stackTraceToString())
            e.printStackTrace()
        }
    }

    fun debuggerContinue() {
        if (DebuggerAbility.Continue !in debugger.abilities) {
            throw IllegalStateException("impossible to call continue, debugger can not do that")
        }
        try {
            debugger.cont()
        } catch (e: Exception) {
            logError(e.stackTraceToString())
            e.printStackTrace()
        }
    }
}

private fun NPIDE.log(message: String, messageType: Console.MessageType) {
    val npideName = "NPIDE"
    console.log(npideName, message, messageType)
}

private fun NPIDE.logError(message: String) = log(message, Console.MessageType.Error)

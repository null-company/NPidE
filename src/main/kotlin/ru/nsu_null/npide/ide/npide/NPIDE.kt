package ru.nsu_null.npide.ide.npide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.nsu_null.npide.ide.config.ConfigManager
import ru.nsu_null.npide.ide.console.Console
import ru.nsu_null.npide.ide.console.ConsoleLogger
import ru.nsu_null.npide.ide.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ide.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ide.projectchooser.ProjectChooser.Project
import ru.nsu_null.npide.ide.projectstrategies.BuilderStrategy
import ru.nsu_null.npide.ide.projectstrategies.DebuggerStrategy
import ru.nsu_null.npide.ide.projectstrategies.ProjectStrategyContext
import ru.nsu_null.npide.ide.projectstrategies.RunnerStrategy
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
        builder.build(
            enableDebugInfo,
            context,
            buildStrategyInfo.extraConfiguration,
            breakPoints,
            dirtyFlags,
            consoleLogger
        )
    }

    fun runCurrentProject() {
        val context = ProjectStrategyContext.fromProjectConfig(configManager.currentProjectConfig)
        val buildStrategyInfo = configManager.currentLanguageDistributionInfo.buildStrategy
        val consoleLogger = ConsoleLogger(console)
        runner.run(
            context,
            buildStrategyInfo.extraConfiguration,
            consoleLogger
        )
    }

    fun debugCurrentProject() {
        val context = ProjectStrategyContext.fromProjectConfig(configManager.currentProjectConfig)
        val buildStrategyInfo = configManager.currentLanguageDistributionInfo.buildStrategy
        val breakPoints = projectStorage.breakpointStorage.breakPoints
        val consoleLogger = ConsoleLogger(console)
        debugger.debug(
            context,
            buildStrategyInfo.extraConfiguration,
            breakPoints,
            consoleLogger
        )
    }
}

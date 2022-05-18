package ru.nsu_null.npide.ui.npide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ui.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ui.projectchooser.ProjectChooser.Project

object NPIDE {
    var state: State by mutableStateOf(CHOOSING_PROJECT)
        private set

    enum class State {
        CHOOSING_PROJECT,
        IN_PROJECT
    }

    var currentProject: Project? = null
    lateinit var configManager: ConfigManager

    lateinit var console: Console

    fun openProject(project: Project) {
        currentProject = project
        configManager = ConfigManager(project)
        console = Console()
        state = IN_PROJECT
    }

    fun openChooseProject() {
        currentProject = null
        state = CHOOSING_PROJECT
    }
}

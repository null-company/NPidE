package ru.nsu_null.npide.ui.npide

import androidx.compose.runtime.mutableStateOf
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.npide.NPIDE.State.CHOOSING_PROJECT
import ru.nsu_null.npide.ui.npide.NPIDE.State.IN_PROJECT
import ru.nsu_null.npide.ui.projectchooser.ProjectChooser.Project

object NPIDE {
    @Suppress("ObjectPropertyName")
    private var _state = mutableStateOf(CHOOSING_PROJECT)
    var state: State = CHOOSING_PROJECT
        private set
        get() = _state.value

    enum class State {
        CHOOSING_PROJECT,
        IN_PROJECT
    }

    var currentProject: Project? = null
    lateinit var configManager: ConfigManager

    fun openProject(project: Project) {
        currentProject = project
        configManager = ConfigManager(project)
        _state.value = IN_PROJECT
    }

    fun openChooseProject() {
        currentProject = null
        _state.value = CHOOSING_PROJECT
    }
}

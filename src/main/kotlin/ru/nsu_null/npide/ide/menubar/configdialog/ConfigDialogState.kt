package ru.nsu_null.npide.ide.menubar.configdialog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.nsu_null.npide.ide.config.ConfigManager
import ru.nsu_null.npide.ide.config.GrammarConfig
import ru.nsu_null.npide.ide.config.LanguageDistributionInfo
import ru.nsu_null.npide.ide.config.ProjectConfig
import ru.nsu_null.npide.ide.npide.NPIDE

internal data class ProjectConfigState(
    val projectName: MutableState<String>,
    val entryPoint: MutableState<String>,
    val languageDistribution: MutableState<String>,
    val projectFiles: MutableState<List<String>>
)

internal data class ConfigDialogState(
    val projectConfig: ProjectConfigState,
    val selection: SelectionState
)

internal data class SelectionState(
    val newProjectFile: MutableState<String>,
    val languageDistribution: MutableState<String>
)

internal fun getStateByConfig(): ConfigDialogState {

    val currentProjectConfig = NPIDE.configManager.currentProjectConfig

    return with(currentProjectConfig) {
        ConfigDialogState(
            ProjectConfigState(
                mutableStateOf(projectName),
                mutableStateOf(entryPoint),
                mutableStateOf(languageDistribution),
                mutableStateOf(projectFiles),
            ),
            SelectionState(
                mutableStateOf(""),
                mutableStateOf("")
            ),
        )
    }
}

internal fun projectConfigByState(configDialogState: ConfigDialogState): ProjectConfig {
    return with(configDialogState.projectConfig) {
        ProjectConfig(
            projectName.value,
            entryPoint.value,
            languageDistribution.value,
            projectFiles.value
        )
    }
}

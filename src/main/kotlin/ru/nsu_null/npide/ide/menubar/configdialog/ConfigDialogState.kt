package ru.nsu_null.npide.ide.menubar.configdialog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.nsu_null.npide.ide.config.ConfigManager
import ru.nsu_null.npide.ide.npide.NPIDE

internal data class CurrentSelectionsState(
    val highlighterPath: MutableState<String>,
    val grammarPath: MutableState<String>,
    val grammarExtension: MutableState<String>,
    val projectFilePath: MutableState<String>
)

internal data class ProjectConfigState(
    val runPath: MutableState<String>,
    val buildPath: MutableState<String>,
    val debugPath: MutableState<String>,
    val grammarConfigs: MutableState<List<ConfigManager.GrammarConfig>>,
    val projectFilePaths: MutableState<List<String>>
)

internal data class ConfigDialogState(
    val projectConfig: ProjectConfigState,
    val selectionState: CurrentSelectionsState
)

internal fun getStateByConfig(): ConfigDialogState {

    val currentProjectConfig = NPIDE.configManager.currentProjectConfig

    val runPath = currentProjectConfig.run
    val buildPath = currentProjectConfig.build
    val debugPath = currentProjectConfig.debug
    val grammarConfigs = currentProjectConfig.grammarConfigs
    val projectFilePaths = currentProjectConfig.projectFilePaths


    return ConfigDialogState(
        ProjectConfigState(
            mutableStateOf(runPath),
            mutableStateOf(buildPath),
            mutableStateOf(debugPath),
            mutableStateOf(grammarConfigs),
            mutableStateOf(projectFilePaths)
        ),
        CurrentSelectionsState(
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf("")
        ),
    )
}

internal fun projectConfigByState(configDialogState: ConfigDialogState): ConfigManager.ProjectConfig {
    return ConfigManager.ProjectConfig(
        configDialogState.projectConfig.buildPath.value,
        configDialogState.projectConfig.runPath.value,
        configDialogState.projectConfig.debugPath.value,
        NPIDE.configManager.currentProjectConfig.filePathToDirtyFlag,
        configDialogState.projectConfig.projectFilePaths.value,
        configDialogState.projectConfig.grammarConfigs.value
    )
}

internal fun GrammarConfig(configurationState: ConfigDialogState): ConfigManager.GrammarConfig {
    return ConfigManager.GrammarConfig(
        configurationState.selectionState.grammarExtension.value,
        configurationState.selectionState.grammarPath.value,
        configurationState.selectionState.highlighterPath.value
    )
}

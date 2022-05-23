package ru.nsu_null.npide.ui.menubar.configdialog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.npide.NPIDE

internal data class CurrentSelectionsState(
    val highlighterPath: MutableState<String>,
    val grammarPath: MutableState<String>,
    val grammarExtension: MutableState<String>,
    val projectFilePath: MutableState<String>
)

internal data class ProjectConfigState(
    val delegatesConfigPath: MutableState<String>,
    val grammarConfigs: MutableState<List<ConfigManager.GrammarConfig>>,
    val projectFilePaths: MutableState<List<String>>
)

internal data class ConfigDialogState(
    val projectConfig: ProjectConfigState,
    val selectionState: CurrentSelectionsState
)

internal fun getStateByConfig(): ConfigDialogState {

    val currentProjectConfig = NPIDE.configManager.currentProjectConfig

    val delegatesConfigPath = currentProjectConfig.pathToDelegatesConfig
    val grammarConfigs = currentProjectConfig.grammarConfigs
    val projectFilePaths = currentProjectConfig.projectFilePaths


    return ConfigDialogState(
        ProjectConfigState(
            mutableStateOf(delegatesConfigPath),
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
        configDialogState.projectConfig.delegatesConfigPath.value,
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

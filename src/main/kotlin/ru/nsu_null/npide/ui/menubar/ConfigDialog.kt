package ru.nsu_null.npide.ui.menubar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberDialogState
import ru.nsu_null.npide.ui.config.ConfigManager
import ru.nsu_null.npide.ui.menubar.ConfigureProjectAction.*
import javax.swing.JFileChooser

private fun applyConfig(config: ConfigManager.ProjectConfig) {
    ConfigManager.currentProjectConfig = ConfigManager.AutoUpdatedProjectConfig(config)
}

private fun applyCommonPath(configDialogState: ConfigDialogState, path: String) {
    configDialogState.projectConfig.buildPath.value = path
    configDialogState.projectConfig.runPath.value = path
    configDialogState.projectConfig.debugPath.value = path
}

private data class CurrentSelectionsState(
    val highlighterPath: MutableState<String>,
    val grammarPath: MutableState<String>,
    val grammarExtension: MutableState<String>
)

private data class ProjectConfigState(
    val runPath: MutableState<String>,
    val buildPath: MutableState<String>,
    val debugPath: MutableState<String>,
    val grammarConfigs: MutableState<List<ConfigManager.GrammarConfig>>
)

private data class ConfigDialogState(
    val projectConfig: ProjectConfigState,
    val selectionState: CurrentSelectionsState
)

private fun getStateByConfig(): ConfigDialogState {

    val currentProjectConfig = ConfigManager.currentProjectConfig

    val runPath = currentProjectConfig.run
    val buildPath = currentProjectConfig.build
    val debugPath = currentProjectConfig.debug
    val grammarConfigs = currentProjectConfig.grammarConfigs

    return ConfigDialogState(
        ProjectConfigState(
            mutableStateOf(runPath),
            mutableStateOf(buildPath),
            mutableStateOf(debugPath),
            mutableStateOf(grammarConfigs)
        ),
        CurrentSelectionsState(
            mutableStateOf(""),
            mutableStateOf(""),
            mutableStateOf(""),
        ),
    )
}

private fun projectConfigByState(configDialogState: ConfigDialogState): ConfigManager.ProjectConfig {
    return ConfigManager.ProjectConfig(
        configDialogState.projectConfig.buildPath.value,
        configDialogState.projectConfig.runPath.value,
        configDialogState.projectConfig.debugPath.value,
        ConfigManager.currentProjectConfig.filePathToDirtyFlag,
        ConfigManager.currentProjectConfig.projectFilePaths,
        configDialogState.projectConfig.grammarConfigs.value
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfigDialog(isOpen: MutableState<Boolean>){
    val stateVertical = rememberScrollState(0)
    Dialog(onCloseRequest = { isOpen.value = false },
        title = "Configuration of project",
        resizable = false,
        state = rememberDialogState(size = WindowSize(1280.dp, 720.dp))
    ) {

        val configurationState = remember { getStateByConfig() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(stateVertical)
                .padding(end = 12.dp, bottom = 12.dp)
        ) {
            Column {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            applyConfig(projectConfigByState(configurationState))
                            isOpen.value = false
                        },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Apply config")
                    }
                }
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Configuration of Build:")
                    SimpleOutlinedTextFieldSample("Build Path", configurationState.projectConfig.buildPath)
                    Button(
                        onClick = { chooseFile(ChooseBuild, configurationState) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("...")
                    }
                    Button(
                        onClick = { applyCommonPath(configurationState, configurationState.projectConfig.buildPath.value) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Apply to all")
                    }

                }
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Configuration of Run:")
                    SimpleOutlinedTextFieldSample("Run Path", configurationState.projectConfig.runPath)
                    Button(
                        onClick = { chooseFile(ChooseRun, configurationState) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("...")
                    }
                    Button(
                        onClick = { applyCommonPath(configurationState, configurationState.projectConfig.runPath.value) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Apply to all")
                    }

                }
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Configuration of Debug:")
                    SimpleOutlinedTextFieldSample("Debug Path", configurationState.projectConfig.debugPath)
                    Button(
                        onClick = { chooseFile(ChooseDebug, configurationState) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("...")
                    }
                    Button(
                        onClick = { applyCommonPath(configurationState, configurationState.projectConfig.debugPath.value) },
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("Apply to all")
                    }

                }
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp, bottom = 12.dp)
                        .background(color = Color(0, 0, 0, 20))

                ) {
                    Text("Configuration of Grammar:",
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        SimpleOutlinedTextFieldSample("Extension of grammar",
                            configurationState.selectionState.grammarExtension)
                        Column(modifier = Modifier.padding(40.dp)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                SimpleOutlinedTextFieldSample("Grammar Path",
                                    configurationState.selectionState.grammarPath)
                                Button(
                                    onClick = { chooseFile(ChooseGrammar, configurationState) },
                                    modifier = Modifier.padding(20.dp)
                                ) {
                                    Text("...")
                                }
                            }
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                SimpleOutlinedTextFieldSample("Syntax Highlighter Path",
                                    configurationState.selectionState.highlighterPath)
                                Button(
                                    onClick = { chooseFile(ChooseSyntaxHighlighter, configurationState) },
                                    modifier = Modifier.padding(20.dp)

                                ) {
                                    Text("...")
                                }
                            }
                            Button(
                                onClick = {
                                    if (configurationState.selectionState.grammarExtension.value.isNotBlank()
                                        && configurationState.selectionState.grammarPath.value.isNotBlank()
                                        && configurationState.selectionState.highlighterPath.value.isNotBlank()) {
                                        configurationState.projectConfig.grammarConfigs.value +=
                                            ConfigManager.GrammarConfig(configurationState
                                                .selectionState.grammarExtension.value,
                                                configurationState.selectionState.grammarPath.value,
                                                configurationState.selectionState.highlighterPath.value)
                                    }
                                },
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Text("Add grammar config")
                            }
                        }
                    }
                }
                for (value in configurationState.projectConfig.grammarConfigs.value) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextBox("$value")
                        Button(
                            onClick = { configurationState.projectConfig.grammarConfigs.value -= value }
                        ) {
                            Text("Delete")
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}

@Composable
fun TextBox(text: String = "Item") {
    Box(
        modifier = Modifier.height(52.dp)
            .width((text.length * 6).dp + 40.dp)
            .background(color = Color(0, 0, 0, 20))
            .padding(start = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text)
    }
}

@ExperimentalComposeUiApi
@Composable
fun SimpleOutlinedTextFieldSample(labelText: String, valueText: MutableState<String>) {
    OutlinedTextField(
        value = valueText.value,
        onValueChange = { valueText.value = it },
        label = { Text(labelText,fontWeight = FontWeight.Bold,  color = Color.Black) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White)
    )
}

private enum class ConfigureProjectAction {
    ChooseBuild,
    ChooseRun,
    ChooseDebug,
    ChooseGrammar,
    ChooseSyntaxHighlighter,
}

//TODO: FileChooser with updating text in field
private fun chooseFile(configButtonState: ConfigureProjectAction, dialogState: ConfigDialogState){
    JFileChooser(System.getProperty("user.home")).apply {
        showOpenDialog(null)
        if (selectedFile != null) {
            when (configButtonState) {
                ChooseBuild -> {
                    dialogState.projectConfig.buildPath.value = selectedFile.toString()
                }
                ChooseRun -> {
                    dialogState.projectConfig.runPath.value = selectedFile.toString()
                }
                ChooseDebug -> {
                    dialogState.projectConfig.debugPath.value = selectedFile.toString()
                }
                ChooseGrammar -> {
                    dialogState.selectionState.grammarPath.value = selectedFile.toString()
                }
                ChooseSyntaxHighlighter -> {
                    dialogState.selectionState.highlighterPath.value = selectedFile.toString()
                }
            }
        }
    }
}
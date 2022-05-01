package ru.nsu_null.npide.ui.menubar.configdialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import ru.nsu_null.npide.ui.menubar.configdialog.ConfigureProjectAction.*
import ru.nsu_null.npide.ui.menubar.configdialog.ConfigureProjectAction.Companion.actionToConfigParamAsString
import ru.nsu_null.npide.ui.npide.NPIDE
import javax.swing.JFileChooser

private fun applyConfig(config: ConfigManager.ProjectConfig) {
    NPIDE.configManager.currentProjectConfig = NPIDE.configManager.AutoUpdatedProjectConfig(config)
}

private fun applyCommonPath(configDialogState: ConfigDialogState, path: String) {
    configDialogState.projectConfig.buildPath.value = path
    configDialogState.projectConfig.runPath.value = path
    configDialogState.projectConfig.debugPath.value = path
}

@ExperimentalComposeUiApi
@Composable
fun ConfigDialog(isOpen: MutableState<Boolean>) {
    val stateVertical = rememberScrollState(0)

    Dialog(
        onCloseRequest = { isOpen.value = false },
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
                ApplyConfigButton(isOpen, configurationState)

                AddAllConfigItem(
                    configurationState,
                    configurationState.projectConfig.buildPath,
                    ChooseBuild
                )
                AddAllConfigItem(
                    configurationState,
                    configurationState.projectConfig.runPath,
                    ChooseRun
                )
                AddAllConfigItem(
                    configurationState,
                    configurationState.projectConfig.debugPath,
                    ChooseDebug
                )

                GrammarConfigurationForm(configurationState)
                CurrentlySelectedGrammarsList(configurationState)
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}


@ExperimentalComposeUiApi
@Composable
private fun AddAllConfigItem(
    configurationState: ConfigDialogState,
    configField: MutableState<String>,
    action: ConfigureProjectAction
) {
    SimpleConfigItem(configurationState, configField, action) {
        Button(
            onClick = { applyCommonPath(configurationState, configField.value) },
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Apply to all")
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleConfigItem(
    configurationState: ConfigDialogState,
    configField: MutableState<String>,
    action: ConfigureProjectAction,
    afterChooseFileButton: (@Composable @ExtensionFunctionType RowScope.() -> Unit)? = null
) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val configPropertyName = actionToConfigParamAsString[action]!!
        Text("Configuration of $configPropertyName:")
        SimpleOutlinedTextFieldSample("$configPropertyName Path", configField)
        Button(
            onClick = { chooseFile(action, configurationState) },
            modifier = Modifier.padding(20.dp)
        ) {
            Text("...")
        }
        afterChooseFileButton?.invoke(this)
    }
}

@Composable
private fun ApplyConfigButton(isOpen: MutableState<Boolean>, configurationState: ConfigDialogState) {
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

@Composable
private fun CurrentlySelectedGrammarsList(configurationState: ConfigDialogState) {
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

@ExperimentalComposeUiApi
@Composable
private fun GrammarConfigurationForm(configurationState: ConfigDialogState) {
    Box(
        modifier = Modifier
            .padding(end = 12.dp, bottom = 12.dp)
            .background(color = Color(0, 0, 0, 20))

    ) {
        Text(
            "Configuration of Grammar:",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )

        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            SimpleOutlinedTextFieldSample(
                "Extension of grammar",
                configurationState.selectionState.grammarExtension
            )
            Column(modifier = Modifier.padding(40.dp)) {
                SimpleConfigItem(
                    configurationState,
                    configurationState.selectionState.grammarPath,
                    ChooseGrammar
                )
                SimpleConfigItem(
                    configurationState,
                    configurationState.selectionState.highlighterPath,
                    ChooseSyntaxHighlighter
                )
                AddGrammarConfigButton(configurationState)
            }
        }
    }
}

@Composable
private fun AddGrammarConfigButton(configurationState: ConfigDialogState) {

    fun selectionIsReady(): Boolean {
        return (configurationState.selectionState.grammarExtension.value.isNotBlank() &&
                configurationState.selectionState.grammarPath.value.isNotBlank() &&
                configurationState.selectionState.highlighterPath.value.isNotBlank())
    }

    Button(
        onClick = {
            if (selectionIsReady()) {
                configurationState.projectConfig.grammarConfigs.value +=
                    GrammarConfig(configurationState)
            }
        },
        modifier = Modifier.padding(20.dp)
    ) {
        Text("Add grammar config")
    }
}

@Composable
private fun TextBox(text: String = "Item") {
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
private fun SimpleOutlinedTextFieldSample(labelText: String, valueText: MutableState<String>) {
    OutlinedTextField(
        value = valueText.value,
        onValueChange = { valueText.value = it },
        label = { Text(labelText, fontWeight = FontWeight.Bold, color = Color.Black) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White
        )
    )
}

private enum class ConfigureProjectAction {
    ChooseBuild,
    ChooseRun,
    ChooseDebug,
    ChooseGrammar,
    ChooseSyntaxHighlighter;

    companion object {
        val actionToConfigParamAsString = mapOf(
            ChooseBuild to "Build",
            ChooseRun to "Run",
            ChooseDebug to "Debug",
            ChooseGrammar to "Grammar",
            ChooseSyntaxHighlighter to "Syntax Highlighter"
        )
    }
}

//TODO: FileChooser with updating text in field
private fun chooseFile(configButtonState: ConfigureProjectAction, dialogState: ConfigDialogState) {
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
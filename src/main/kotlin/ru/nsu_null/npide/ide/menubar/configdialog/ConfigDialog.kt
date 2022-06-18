package ru.nsu_null.npide.ide.menubar.configdialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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
import ru.nsu_null.npide.ide.config.AutoUpdatedProjectConfig
import ru.nsu_null.npide.ide.config.ProjectConfig
import ru.nsu_null.npide.ide.menubar.configdialog.ConfigureProjectAction.*
import ru.nsu_null.npide.ide.menubar.configdialog.ConfigureProjectAction.Companion.actionToConfigParamAsString
import ru.nsu_null.npide.ide.npide.NPIDE
import javax.swing.JFileChooser

private fun applyConfig(config: ProjectConfig) {
    NPIDE.configManager.currentProjectConfig = AutoUpdatedProjectConfig.forCurrentConfigManager(config)
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

                SimpleConfigTextInputItem(
                    configurationState.projectConfig.projectName
                )
                SimpleConfigTextInputItem(
                    configurationState.projectConfig.entryPoint
                )
                SimpleConfigFileChosenItem(
                    configurationState,
                    configurationState.projectConfig.languageDistribution,
                    ChooseLanguageDistribution
                )

                ProjectConfigForm(configurationState)
                CurrentlySelectedProjectPathsList(configurationState)
                GrammarConfigurationView(configurationState)
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleConfigFileChosenItem(
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SimpleConfigTextInputItem(
    configField: MutableState<String>,
) {
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            configField.value,
            onValueChange = { configField.value = it }
        )
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
private fun AddProjectFileConfigButton(configurationState: ConfigDialogState) {

    fun selectionIsReady(): Boolean {
        return (configurationState.selection.newProjectFile.value.isNotBlank())
    }

    Button(
        onClick = {
            if (selectionIsReady()) {
                configurationState.projectConfig.projectFiles.value +=
                    (configurationState.selection.newProjectFile.value)
            }
        },
        modifier = Modifier.padding(20.dp)
    ) {
        Text("Add project file source path")
    }
}

@Composable
private fun CurrentlySelectedProjectPathsList(configurationState: ConfigDialogState) {
    for (value in configurationState.projectConfig.projectFiles.value) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextBox(value)
            Button(
                onClick = { configurationState.projectConfig.projectFiles.value -= value }
            ) {
                Text("Delete")
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}



@ExperimentalComposeUiApi
@Composable
private fun ProjectConfigForm(configurationState: ConfigDialogState) {
    Box(
        modifier = Modifier
            .padding(end = 12.dp, bottom = 12.dp)
            .background(color = Color(0, 0, 0, 20))

    ) {
        Text(
            "Project sources:",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )

        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            SimpleConfigFileChosenItem(
                configurationState,
                configurationState.selection.newProjectFile,
                ChooseSource
            )
            AddProjectFileConfigButton(configurationState)
        }
    }
}
@ExperimentalComposeUiApi
@Composable
private fun GrammarConfigurationView(configurationState: ConfigDialogState) {
    Box(
        modifier = Modifier
            .padding(end = 12.dp, bottom = 12.dp)
            .background(color = Color(0, 0, 0, 20))

    ) {
        val grammarConfigs = NPIDE.configManager.currentLanguageDistributionInfo.grammarConfigs
        Text(
            "Grammar configs:",
            modifier = Modifier.padding(10.dp),
            fontWeight = FontWeight.Bold
        )

        for (grammarConfig in grammarConfigs) {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextBox("Extension of grammar:")
                TextBox(grammarConfig.sourceFileExtension)
                Column(modifier = Modifier.padding(40.dp)) {
                    TextBox(
                        grammarConfig.grammar
                    )
                    TextBox(
                        grammarConfig.syntaxHighlighter
                    )
                }
            }
        }
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
    ChooseLanguageDistribution,
    ChooseSource;

    companion object {
        val actionToConfigParamAsString = mapOf(
            ChooseSource to "Source file path",
            ChooseLanguageDistribution to "Language distribution file path"
        )
    }
}

//TODO: FileChooser with updating text in field
private fun chooseFile(configButtonState: ConfigureProjectAction, dialogState: ConfigDialogState) {
    JFileChooser(NPIDE.currentProject!!.rootFolder.filepath).apply {
        showOpenDialog(null)
        if (selectedFile != null) {
            val chooseResult = selectedFile.toString()
            when (configButtonState) {
                ChooseSource -> {
                    dialogState.selection.newProjectFile.value += chooseResult
                }
                ChooseLanguageDistribution -> {
                    dialogState.selection.languageDistribution.value = chooseResult
                }
            }
        }
    }
}

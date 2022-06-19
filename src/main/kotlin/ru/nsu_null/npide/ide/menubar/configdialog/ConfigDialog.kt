package ru.nsu_null.npide.ide.menubar.configdialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import org.jetbrains.skia.shaper.Shaper
import ru.nsu_null.npide.ide.common.AppTheme
import ru.nsu_null.npide.ide.config.AutoUpdatedProjectConfig
import ru.nsu_null.npide.ide.config.ProjectConfig
import ru.nsu_null.npide.ide.menubar.configdialog.ConfigureProjectAction.*
import ru.nsu_null.npide.ide.menubar.configdialog.ConfigureProjectAction.Companion.actionToConfigParamAsString
import ru.nsu_null.npide.ide.npide.NPIDE
import javax.swing.JFileChooser

private fun applyConfig(config: ProjectConfig) {
    NPIDE.configManager.currentProjectConfig = AutoUpdatedProjectConfig.forCurrentConfigManager(config)
}

private val textColor = AppTheme.colors.textDark
private val backgroundColor = AppTheme.colors.backgroundDark

@ExperimentalComposeUiApi
@Composable
fun ConfigDialog(isOpen: MutableState<Boolean>) {
    val stateVertical = rememberScrollState(0)

    Dialog(
        onCloseRequest = { isOpen.value = false },
        title = "Configuration of project",
        resizable = false,
        state = rememberDialogState(size = DpSize(1280.dp, 720.dp))
    ) {

        val configurationState = remember { getStateByConfig() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(stateVertical)
                .background(color = backgroundColor)
        ) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)){
                    GeneralConfigItem("Project name: ", 52.dp) {
                        ConfigInputItem(
                            configurationState.projectConfig.projectName
                        )
                    }
                    GeneralConfigItem("Project entry point / main class: ", 52.dp) {
                        ConfigInputItem(
                            configurationState.projectConfig.entryPoint
                        )
                    }

                    SimpleConfigFileChosenItem(
                        configurationState,
                        configurationState.projectConfig.languageDistribution,
                        ChooseLanguageDistribution,
                    )

                    SimpleConfigFileChosenItem(
                        configurationState,
                        configurationState.selection.newProjectFile,
                        ChooseSource
                    ) {
                        AddProjectFileConfigButton(configurationState)
                    }

                    CurrentlySelectedProjectPathsList(configurationState)
                    GrammarConfigurationView(configurationState)
                    ApplyConfigButton(isOpen, configurationState)
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}
@ExperimentalComposeUiApi
@Composable
private fun GeneralConfigItem(
    text: String,
    height: Dp = 52.dp,
    composable: (@Composable @ExtensionFunctionType BoxScope.() -> Unit)
) {
    Row(
        modifier = Modifier.height(height)
            .background(color = Color(0, 0, 0, 40))
            .padding(start=10.dp)
    ) {
        Box(
            modifier = Modifier.height(height)
                .width(400.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text, color = textColor)
        }
        Box(
            modifier = Modifier.height(height),
            contentAlignment = Alignment.CenterStart
        ) {
            composable.invoke(this)
        }

    }
    Row(
        modifier = Modifier.height(4.dp)
    ) { }
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleConfigFileChosenItem(
    configurationState: ConfigDialogState,
    configField: MutableState<String>,
    action: ConfigureProjectAction,
    afterChooseFileButton: (@Composable @ExtensionFunctionType RowScope.() -> Unit)? = null
) {
    val configPropertyName = actionToConfigParamAsString[action]!!
    GeneralConfigItem("Configuration of $configPropertyName:", 100.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SimpleOutlinedTextFieldSample("$configPropertyName path", configField)
            Button(
                onClick = { chooseFile(action, configurationState) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .requiredWidth(60.dp),
            ) {
                Text("...", style = TextStyle(color = textColor))
            }
            afterChooseFileButton?.invoke(this)
        }
    }
}

@Composable
private fun ConfigInputItem(
    configField: MutableState<String>
) {
    TextField(
        configField.value,
        onValueChange = { configField.value = it },
        textStyle = TextStyle(color = textColor),
    )
}

@Composable
private fun ApplyConfigButton(isOpen: MutableState<Boolean>, configurationState: ConfigDialogState) {
    Button(
        onClick = {
            applyConfig(projectConfigByState(configurationState))
            isOpen.value = false
        },
    ) {
        Text("Apply config", style = TextStyle(color = textColor))
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
        modifier = Modifier.padding(20.dp).requiredWidth(200.dp)
    ) {
        Text("Add project file source path", style = TextStyle(color = textColor))
    }
}

@Composable
private fun CurrentlySelectedProjectPathsList(configurationState: ConfigDialogState) {
    Column(
        modifier = Modifier.border(
            width = 2.dp,
            color = Color(0xAAFFFFFF))
    ) {
        for (value in configurationState.projectConfig.projectFiles.value) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(400.dp)) {
                    TextBox(value)
                }
                Button(
                    onClick = { configurationState.projectConfig.projectFiles.value -= value }
                ) {
                    Text("Delete", style = TextStyle(color = textColor))
                }
            }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Grammar configs loaded from language distribution:",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 40.dp),
                fontWeight = FontWeight.Bold,
                style = TextStyle(color = textColor)
            )

            Column {
                for (grammarConfig in grammarConfigs) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextBox(grammarConfig.sourceFileExtension)
                        Column(modifier = Modifier.padding(8.dp)) {
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
    }
}

@Composable
private fun TextBox(text: String = "Item") {
    Box(
        modifier = Modifier.height(52.dp)
            .background(color = Color(0, 0, 0, 20))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, style = TextStyle(color = textColor))
    }
}

@ExperimentalComposeUiApi
@Composable
private fun SimpleOutlinedTextFieldSample(labelText: String, valueText: MutableState<String>) {
    Box(modifier = Modifier.width(400.dp)){
        OutlinedTextField(
            value = valueText.value,
            onValueChange = { valueText.value = it },
            label = { Text(labelText, fontWeight = FontWeight.Bold, color = textColor) },
            textStyle = TextStyle(color = textColor, fontWeight = FontWeight.Bold),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                backgroundColor = backgroundColor
            ),
            singleLine = true
        )
    }
}

private enum class ConfigureProjectAction {
    ChooseLanguageDistribution,
    ChooseSource;

    companion object {
        val actionToConfigParamAsString = mapOf(
            ChooseSource to "Source file",
            ChooseLanguageDistribution to "Language distribution file"
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
                    dialogState.projectConfig.languageDistribution.value = chooseResult
                }
            }
        }
    }
}

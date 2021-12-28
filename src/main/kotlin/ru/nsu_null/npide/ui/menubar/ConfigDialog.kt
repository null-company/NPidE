package ru.nsu_null.npide.ui.menubar

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import ru.nsu_null.npide.ui.yaml.ConfigManager
import ru.nsu_null.npide.ui.yaml.ConfigManager.ProjectFileConfigFile
import javax.swing.JFileChooser

class ConfigDialog {
    private var grammarPath = ""
    private var syntaxHighlighterPath = ""

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun run(stateOpen: MutableState<Boolean>){
        val stateVertical = rememberScrollState(0)
        var removeListElement by mutableStateOf(-1)
        Dialog(onCloseRequest = { stateOpen.value = false},
            title = "Configuration of project",
            resizable = false,
            state = rememberDialogState(size = WindowSize(1280.dp, 720.dp))
        ) {

            // Run, Build, Debug == path
            // [SyntaxHighl]

            if(removeListElement != -1){
                println(ConfigManager.grammarConfig[removeListElement])
                ConfigManager.grammarConfig.removeAt(removeListElement)
                ConfigManager.storeConfig()
                removeListElement = -1
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(stateVertical)
                    .padding(end = 12.dp, bottom = 12.dp)
            ) {
                Column {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Configuration of Build:")
                        SimpleOutlinedTextFieldSample("Build Path", ConfigManager.buildPath)
                        Button(
                            onClick = { fileChooser(0) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("...")
                        }
                        Button(
                            onClick = { ConfigManager.applyToAll(0) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("Apply to all")
                        }

                    }
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Configuration of Run:")
                        SimpleOutlinedTextFieldSample("Run Path", ConfigManager.runPath)
                        Button(
                            onClick = { fileChooser(1) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("...")
                        }
                        Button(
                            onClick = { ConfigManager.applyToAll(1) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("Apply to all")
                        }

                    }
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Configuration of Debug:")
                        SimpleOutlinedTextFieldSample("Debug Path", ConfigManager.debugPath)
                        Button(
                            onClick = { fileChooser(2) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("...")
                        }
                        Button(
                            onClick = { ConfigManager.applyToAll(2) },
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text("Apply to all")
                        }

                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 12.dp)
                            .background(color = Color(0, 0, 0, 20))

                    ) {Text("Configuration of Grammar:",
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            val ext = SimpleOutlinedTextFieldSample("Extension of grammar", "")
                            Column(modifier = Modifier.padding(40.dp)) {
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    SimpleOutlinedTextFieldSample("Grammar Path", grammarPath)
                                    Button(
                                        onClick = { fileChooser(3) },
                                        modifier = Modifier.padding(20.dp)
                                    ) {
                                        Text("...")
                                    }
                                }
                                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    SimpleOutlinedTextFieldSample("Syntax Highlighter Path", syntaxHighlighterPath)
                                    Button(
                                        onClick = { fileChooser(4) },
                                        modifier = Modifier.padding(20.dp)

                                    ) {
                                        Text("...")
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (ext.isNotBlank() && grammarPath.isNotBlank() && syntaxHighlighterPath.isNotBlank()) {
                                            ConfigManager.setGrammar(ext, grammarPath, syntaxHighlighterPath)
                                        }
                                    },
                                    modifier = Modifier.padding(20.dp)

                                ) {
                                    Text("Add grammar config")
                                }
                            }
                        }
                    }
                        for (item in ConfigManager.grammarConfig) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            TextBox("$item")
                            Button(
                                onClick = {removeListElement = ConfigManager.grammarConfig.indexOf(item)},
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
    fun SimpleOutlinedTextFieldSample(labelText: String, valueText: String): String {
        var text by remember { mutableStateOf("") }
        text = valueText
        OutlinedTextField(
            value = text,
            onValueChange = {text = it },
            label = { Text(labelText,fontWeight = FontWeight.Bold,  color = Color.Black) },
            textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                backgroundColor = Color.White)
        )
        return text
    }

    //TODO: FileChooser with updating text in field
    fun fileChooser(configButtonState :Int){
        JFileChooser(System.getProperty("user.home")).apply {
            showOpenDialog(null)
            if (selectedFile != null) {
                when (configButtonState) {
                    0 -> {
                        ConfigManager.buildPath = selectedFile.toString()
                        ConfigManager.storeConfig()
                    }
                    1 -> {
                        ConfigManager.runPath = selectedFile.toString()
                        ConfigManager.storeConfig()
                    }
                    2 -> {
                        ConfigManager.debugPath = selectedFile.toString()
                        ConfigManager.storeConfig()
                    }
                    3 -> {
                        grammarPath = selectedFile.toString()
                    }
                    4 -> {
                        syntaxHighlighterPath= selectedFile.toString()
                    }
                }
            }
        }
    }
}
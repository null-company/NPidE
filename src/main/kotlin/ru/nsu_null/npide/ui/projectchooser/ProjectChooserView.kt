package ru.nsu_null.npide.ui.projectchooser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.npide.NPIDE
import ru.nsu_null.npide.ui.projectchooser.ProjectChooser.Project

@Composable
fun ProjectChooserView() {
    val projectChooser = remember { ProjectChooser(DiskHomeDirectoryRepositoryManager()) }
    val availableProjects = projectChooser.availableProjects
    Row(
        Modifier.background(AppTheme.colors.backgroundDark)
    ) {
        Column {
            val newProjectPath = remember { mutableStateOf("") }
            OutlinedTextField(
                value = newProjectPath.value,
                onValueChange = { newProjectPath.value = it },
                label = { Text("New project path", fontWeight = FontWeight.Bold, color = Color.Gray) },
                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Light),
                modifier = Modifier.padding(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    backgroundColor = Color.White
                )
            )
            Button(
                onClick = {
                    val newProject = Project(newProjectPath.value)
                    projectChooser.addProject(newProject)
                    NPIDE.openProject(newProject)
                },
                content = {
                    Text("Add", color = Color.White)
                },
            )
        }

        val scrollState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(availableProjects.value.size) { index ->
                val project = availableProjects.value[index]
                Row {
                    Button(
                        onClick = { NPIDE.openProject(project) },
                        content = {
                            Text(project.rootFolder.filepath, color = Color.White)
                        },
                    )
                    Button(
                        onClick = { projectChooser.deleteProject(project) },
                        content = {
                            Text("X")
                        }
                    )
                }
            }
        }

        VerticalScrollbar(
            Modifier.align(Alignment.CenterVertically),
            scrollState,
            availableProjects.value.size,
            5.dp
        )
    }
}

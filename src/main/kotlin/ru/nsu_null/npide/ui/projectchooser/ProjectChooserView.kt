package ru.nsu_null.npide.ui.projectchooser

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nsu_null.npide.platform.HomeFolder
import ru.nsu_null.npide.platform.toJavaFile
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.npide.NPIDE
import ru.nsu_null.npide.ui.projectchooser.ProjectChooser.Project
import javax.swing.JFileChooser

@Preview
@Composable
fun ProjectChooserView() {
    val projectChooser = remember { ProjectChooser(DiskHomeDirectoryRepositoryManager()) }
    val availableProjects = projectChooser.availableProjects
    val grayButtonColor = buttonColors(Color.Gray)
    Row(
        Modifier.background(AppTheme.colors.backgroundDark).fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource("npide.png"),
                contentDescription = "NPidE_Logo"
            )

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier.height(50.dp)
                    .width(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ){
                Text("NPIDE", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize= 20.sp, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    val newProject =
                        Project(chooseProjectFolderDialog() ?: return@Button)
                    projectChooser.addProject(newProject)
                    NPIDE.openProject(newProject)
                },
                colors = grayButtonColor,
                content = {
                    Text("Add new project", color = Color.White)
                },
            )
        }

        // note this likely break when there are many projects, todo introduce lazycolumn
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (project in availableProjects.value) {
                Box(Modifier.fillMaxWidth(0.4f)) {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 50.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { NPIDE.openProject(project) },
                            content = {
                                Text(project.rootFolder.filepath, color = Color.White)
                            },
                            colors = grayButtonColor
                        )
                        Button(
                            onClick = { projectChooser.deleteProject(project) },
                            content = {
                                Text("X")
                            },
                            colors = grayButtonColor
                        )
                    }
                }
            }
        }
    }
}

// todo this should probably be quite reusable
private fun chooseProjectFolderDialog(): String? {
    // even though looks bad on windows, that's the only fast workable workaround
    val f = JFileChooser()
    f.currentDirectory = HomeFolder.toJavaFile()
    f.dialogTitle = "New project folder"
    f.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    f.showDialog(null, "Choose project directory")
    return f.selectedFile?.absolutePath
}

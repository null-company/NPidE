package ru.nsu_null.npide.ui.projectchooser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.npide.NPIDE

@Composable
fun ProjectChooserView() {
    val projectChooser = remember { ProjectChooser(DiskHomeDirectoryRepositoryManager()) }
    val availableProjects = projectChooser.availableProjects
    Box(
        Modifier.fillMaxSize()
            .background(AppTheme.colors.backgroundDark)
    ) {
        val scrollState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            items(availableProjects.size) { index ->
                val project = availableProjects[index]
                Box {
                    Button(
                        onClick = { NPIDE.openProject(project) },
                        content = {
                            Text(project.rootFolder.filepath, color = Color.White)
                        },
                    )
                }
            }
        }

        VerticalScrollbar(
            Modifier.align(Alignment.CenterEnd),
            scrollState,
            availableProjects.size,
            5.dp
        )
    }
}

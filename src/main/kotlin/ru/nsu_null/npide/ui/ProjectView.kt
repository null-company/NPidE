package ru.nsu_null.npide.ui

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.console.Console
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.filetree.FileTree
import ru.nsu_null.npide.ui.npide.NPIDE

@ExperimentalFoundationApi
@Composable
fun ProjectView() {
    val codeViewer = remember {
        val editors = Editors()
        CodeViewer(
            editors = editors,
            fileTree = FileTree(NPIDE.currentProject!!.rootFolder, editors),
            settings = Settings()
        )
    }

    DisableSelection {
        MaterialTheme(
            colors = AppTheme.colors.material
        ) {
            DesktopMaterialTheme {
                Surface {
                    CodeViewerView(codeViewer)
                }
            }
        }
    }
}

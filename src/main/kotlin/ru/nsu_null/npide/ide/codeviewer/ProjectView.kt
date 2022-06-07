package ru.nsu_null.npide.ide.codeviewer

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.nsu_null.npide.ide.common.AppTheme
import ru.nsu_null.npide.ide.common.Settings
import ru.nsu_null.npide.ide.editor.Editors
import ru.nsu_null.npide.ide.filetree.FileTree
import ru.nsu_null.npide.ide.npide.NPIDE

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

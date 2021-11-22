import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import ru.nsu_null.npide.platform.HomeFolder
import ru.nsu_null.npide.platform.PlatformTheme
import ru.nsu_null.npide.ui.CodeViewer
import ru.nsu_null.npide.ui.CodeViewerView
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.filetree.FileTree

@Composable
fun MainView() {
    val codeViewer = remember {
        val editors = Editors()

        CodeViewer(
            editors = editors,
            fileTree = FileTree(HomeFolder, editors),
            settings = Settings()
        )
    }

    DisableSelection {
        MaterialTheme(
            colors = AppTheme.colors.material
        ) {
            PlatformTheme {
                Surface {
                    CodeViewerView(codeViewer)
                }
            }
        }
    }
}
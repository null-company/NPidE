package ru.nsu_null.npide.ide

import ru.nsu_null.npide.ide.common.Settings
import ru.nsu_null.npide.ide.editor.Editors
import ru.nsu_null.npide.ide.filetree.FileTree

class CodeViewer(
    val editors: Editors,
    val fileTree: FileTree,
    val settings: Settings,
)

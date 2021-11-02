package ru.nsu_null.npide.ui

import ru.nsu_null.npide.ui.common.Settings
import ru.nsu_null.npide.ui.editor.Editors
import ru.nsu_null.npide.ui.filetree.FileTree

class CodeViewer(
    val editors: Editors,
    val fileTree: FileTree,
    val settings: Settings
)
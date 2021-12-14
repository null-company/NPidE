package ru.nsu_null.npide.ui.filetree

import OpenCreteFileDialog
import OpenDeleteDialog
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import ru.nsu_null.npide.platform.File
import ru.nsu_null.npide.ui.editor.Editors

class ExpandableFile(
    val file: File,
    val level: Int,
) {
    var children: List<ExpandableFile> by mutableStateOf(emptyList())
    val canExpand: Boolean get() = file.hasChildren

    fun toggleExpanded() {
        children = if (children.isEmpty()) {
            file.children
                .map { ExpandableFile(it, level + 1) }
                .sortedWith(compareBy({ it.file.isDirectory }, { it.file.name }))
                .sortedBy { !it.file.isDirectory }
        } else {
            emptyList()
        }
    }
}

class FileTree(root: File, private val editors: Editors) {
    private val expandableRoot = ExpandableFile(root, 0).apply {
        toggleExpanded()
    }

    val items: List<Item> get() = expandableRoot.toItems()

    inner class Item constructor(
        private val file: ExpandableFile
    ) {
        val name: String get() = file.file.name

        val level: Int get() = file.level

        val type: ItemType
            get() = if (file.file.isDirectory) {
                ItemType.Folder(isExpanded = file.children.isNotEmpty(), canExpand = file.canExpand)
            } else {
                ItemType.File(ext = file.file.name.substringAfterLast(".").lowercase())
            }

        fun open() = when (type) {
            is ItemType.Folder -> file.toggleExpanded()
            is ItemType.File -> editors.open(file.file)
        }

        @OptIn(ExperimentalComposeUiApi::class)
        @Composable
        fun createFile(state:MutableState<Boolean>) = when (type) {
            is ItemType.Folder -> OpenCreteFileDialog(state, file.file.filepath)
            is ItemType.File -> OpenCreteFileDialog(state, file.file.parentPath)
            }


        @OptIn(ExperimentalComposeUiApi::class)
        @Composable
        fun removeFile(state: MutableState<Boolean>) = when (type) {
            is ItemType.Folder -> OpenDeleteDialog(state, file.file.filepath)
            is ItemType.File -> OpenDeleteDialog(state, file.file.filepath)
            }
        }


    sealed class ItemType {
        class Folder(val isExpanded: Boolean, val canExpand: Boolean) : ItemType()
        class File(val ext: String) : ItemType()
    }

    private fun ExpandableFile.toItems(): List<Item> {
        fun ExpandableFile.addTo(list: MutableList<Item>) {
            list.add(Item(this))
            for (child in children) {
                child.addTo(list)
            }
        }

        val list = mutableListOf<Item>()
        addTo(list)
        return list
    }
}

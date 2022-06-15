package ru.nsu_null.npide.ide.filetree

import androidx.compose.runtime.*
import ru.nsu_null.npide.ide.platform.File
import ru.nsu_null.npide.ide.platform.toJavaFile
import ru.nsu_null.npide.ide.platform.toProjectFile
import ru.nsu_null.npide.ide.editor.Editors
import java.util.*

class ExpandableFile(
    val file: File,
    val level: Int,
) {
    var children: List<ExpandableFile> by mutableStateOf(emptyList())
    val canExpand: Boolean get() = file.hasChildren

    val isExpanded: Boolean get() = children.isNotEmpty()

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

    enum class ActionType {
        CreateFolder, CreateFile
    }

    inner class Item constructor(
        private val file: ExpandableFile
    ) {
        val name: String get() = file.file.name

        private fun getParent(): Item {
            if (level == 0) throw IllegalArgumentException("Cannot find parent of root")
            return expandableRoot.toItems().find {
                it.file.level == level - 1
                        && it.file.file.isDirectory
                        && this.file.file.filepath in it.file.file.children.map { file -> file.filepath }
            }!!
        }

        val level: Int get() = file.level

        val type: ItemType
            get() = if (file.file.isDirectory) {
                ItemType.Folder(isExpanded = file.children.isNotEmpty(), canExpand = file.canExpand)
            } else {
                ItemType.File(ext = file.file.name.substringAfterLast(".").lowercase(Locale.getDefault()))
            }

        fun open() = when (type) {
            is ItemType.Folder -> file.toggleExpanded()
            is ItemType.File -> editors.open(file.file)
        }

        /**
         * @param newItemName relative path to the item
         * @param newItemType folder to create folder, file to create file
         */
        fun addItem(newItemName: String, newItemType: ActionType) {
            val newItemAction: (java.io.File) -> Unit = {
                when (newItemType) {
                    ActionType.CreateFile -> it.createNewFile()
                    ActionType.CreateFolder -> it.mkdir()
                }
            }
            when (type) {
                is ItemType.File -> { // create as sibling
                    val parent = getParent()
                    val newItem = java.io.File("${parent.file.file.filepath}/$newItemName")
                        .also(newItemAction).toProjectFile()
                    parent.file.children += ExpandableFile(newItem, level)
                }
                is ItemType.Folder -> { // create as child
                    val newItem = java.io.File("${file.file.filepath}/$newItemName")
                        .also(newItemAction).toProjectFile()
                    if (!file.isExpanded) {
                        file.toggleExpanded()  // expand so user can see the result
                        /** do not add to children because
                         * done implicitly by [ExpandableFile.toggleExpanded] */
                    } else {
                        file.children += ExpandableFile(newItem, level + 1)
                    }
                }
            }
        }

        fun removeItem() {
            getParent().file.children -= this.file
            file.file.toJavaFile().delete()
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

package ru.nsu_null.npide.ui.filetree

import CreateItemDialog
import DeleteItemDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MouseClickScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.mouseClickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nsu_null.npide.platform.VerticalScrollbar
import ru.nsu_null.npide.ui.common.AppTheme
import ru.nsu_null.npide.util.withoutWidthConstraints

@Composable
fun FileTreeViewTabView() = Surface {
    Row(
        Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Files",
            color = LocalContentColor.current.copy(alpha = 0.60f),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun FileTreeView(model: FileTree) = Surface(
    modifier = Modifier.fillMaxSize()
) {
    with(LocalDensity.current) {
        Box {
            val scrollState = rememberLazyListState()
            val fontSize = 14.sp
            val lineHeight = fontSize.toDp() * 1.5f

            LazyColumn(
                modifier = Modifier.fillMaxSize().withoutWidthConstraints(),
                state = scrollState
            ) {
                items(model.items.size) {
                    FileTreeItemView(fontSize, lineHeight, model.items[it])
                }
            }

            VerticalScrollbar(
                Modifier.align(Alignment.CenterEnd),
                scrollState,
                model.items.size,
                lineHeight
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
@Composable
private fun FileTreeItemView(fontSize: TextUnit, height: Dp, model: FileTree.Item) {

    var showContextMenu by remember { mutableStateOf(false) }

    val mouseClickHandler: MouseClickScope.() -> Unit = {
        if (buttons.isPrimaryPressed) model.open()
        if (buttons.isSecondaryPressed) showContextMenu = true
    }
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .mouseClickable(onClick = mouseClickHandler)
            .padding(start = 24.dp * model.level)
            .height(height)
            .fillMaxWidth()
    ) {
        FileTreeDropDownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
            model = model
        )
        FileItemIcon(Modifier.align(Alignment.CenterVertically), model)

        MaterialTheme {
            val active = remember { mutableStateOf(false) }
            Text(
                text = model.name,
                color = if (active.value) LocalContentColor.current.copy(alpha = 0.60f) else LocalContentColor.current,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clipToBounds()
                    .pointerMoveFilter(
                        onEnter = {
                            active.value = true
                            true
                        },
                        onExit = {
                            active.value = false
                            true
                        }
                    ),
                softWrap = true,
                fontSize = fontSize,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileTreeDropDownMenu(expanded: Boolean, onDismissRequest: () -> Unit, model: FileTree.Item) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(AppTheme.colors.backgroundLight)
    ) {
        var showCreateFileDialog by remember { mutableStateOf(false) }
        val fileToCreate = if (showCreateFileDialog) CreateItemDialog(onCloseRequest = onDismissRequest) else null
        fileToCreate?.let {
            model.addItem(it, FileTree.ActionType.CreateFile)
            showCreateFileDialog = false
            onDismissRequest()
        }

        var showCreateFolderDialog by remember { mutableStateOf(false) }
        val folderToCreate = if (showCreateFolderDialog) CreateItemDialog(onCloseRequest = onDismissRequest) else null
        folderToCreate?.let {
            model.addItem(it, FileTree.ActionType.CreateFolder)
            showCreateFolderDialog = false
            onDismissRequest()
        }

        var showDeleteFileDialog by remember { mutableStateOf(false) }
        val deleteFile: Boolean = if (showDeleteFileDialog) {
            DeleteItemDialog(onCloseRequest = onDismissRequest, fullFileName = model.name)
        } else false
        if (deleteFile) {
            model.removeItem()
            showDeleteFileDialog = false
            onDismissRequest()
        }

        // NOTE(Roman Brek) could probably come up with better design to dry this up
        DropdownMenuItem(onClick = { showCreateFileDialog = true }) {
            Text("Create file")
        }
        DropdownMenuItem(onClick = { showCreateFolderDialog = true }) {
            Text("Create directory")
        }
        DropdownMenuItem(onClick = { showDeleteFileDialog = true }) {
            Text("Delete item")
        }
    }
}


@Composable
private fun FileItemIcon(modifier: Modifier, model: FileTree.Item) = Box(modifier.size(24.dp).padding(4.dp)) {
    when (val type = model.type) {
        is FileTree.ItemType.Folder -> when {
            !type.canExpand -> Unit
            type.isExpanded -> Icon(
                Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LocalContentColor.current
            )
            else -> Icon(
                Icons.Default.KeyboardArrowRight, contentDescription = null, tint = LocalContentColor.current
            )
        }
        is FileTree.ItemType.File -> when (type.ext) {
            "clj" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF3E86A0))
            "kt" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF3E86A0))
            "xml" -> Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFFC19C5F))
            "txt" -> Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF87939A))
            "md" -> Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF87939A))
            "gitignore" -> Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color(0xFF87939A))
            "gradle" -> Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF87939A))
            "kts" -> Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF3E86A0))
            "yaml" -> Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF62B543))
            "config" -> Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF62B543))
            "properties" -> Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF62B543))
            "bat" -> Icon(Icons.Default.Launch, contentDescription = null, tint = Color(0xFF87939A))
            else -> Icon(Icons.Default.TextSnippet, contentDescription = null, tint = Color(0xFF87939A))
        }
    }
}
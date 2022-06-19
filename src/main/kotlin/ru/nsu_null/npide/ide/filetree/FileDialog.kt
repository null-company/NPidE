import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.io.File as ioFile


@ExperimentalComposeUiApi
@Composable
fun OutlineFieldItemNameGetter(): String {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Item name", fontWeight = FontWeight.Bold, color = Color.Black) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White
        )
    )
    return text
}

/**
 * In fact is a blocking call to receive a file name
 * @return a name of an item (Folder or File) to create. NOTE: this is not a full path to it
 */
@ExperimentalComposeUiApi
@Composable
fun CreateItemDialog(onCloseRequest: () -> Unit): String? {
// TODO добавить Enter
    var fileName: String? by remember { mutableStateOf(null) }
    Dialog(onCloseRequest = onCloseRequest,
        title = "Choose new item name") {
        val text = OutlineFieldItemNameGetter().ifBlank { null }
        Button(
            onClick = {
                fileName = text
            },
            modifier = Modifier.padding(120.dp)
        ) {
            Text("Create")
        }
    }
    return fileName
}


@ExperimentalComposeUiApi
@Composable
fun DeleteItemDialog(onCloseRequest: () -> Unit, fullFileName: String): Boolean {
// TODO Enter
    var userAnswer by remember { mutableStateOf(false) }
    Dialog(onCloseRequest = onCloseRequest, title = "Delete file?") {
        val buttonModifier = Modifier.padding(60.dp, 100.dp)
        Text(
            text = "Are you sure you want to delete $fullFileName?",
            modifier = Modifier.padding(60.dp, 40.dp),
            color = Color.Black,
            fontSize = 12.sp
        )
        Row {
            Button(
                onClick = {
                    userAnswer = true
                    onCloseRequest()
                },
                modifier = buttonModifier
            ) {
                Text("Delete file")
            }
            Button(
                onClick = {
                    userAnswer = false
                    onCloseRequest()
                },
                modifier = buttonModifier
            ) {
                Text("Cancel")
            }
        }

    }
    return userAnswer
}

fun deleteFile(fileName: String) {
    val file = ioFile(fileName)
    if (file.exists())
        file.deleteRecursively()
}

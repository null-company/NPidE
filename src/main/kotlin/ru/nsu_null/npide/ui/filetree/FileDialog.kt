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
fun SimpleOutlinedTextFieldSample(): String {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Filename",fontWeight = FontWeight.Bold,  color = Color.Black) },
        textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            backgroundColor = Color.White)
    )
    return text
}

fun FileCreate(fileName:String) {
    val file = ioFile(fileName)
    file.writeBytes(ByteArray(0))

}
@ExperimentalComposeUiApi
@Composable
fun OpenCreteFileDialog(state: MutableState<Boolean>, filepath: String) {
// TODO добавить Enter
    Dialog(onCloseRequest={state.value = false}) {
        val text = SimpleOutlinedTextFieldSample()
        Button(
            onClick = {
                val filename = "$filepath/$text"
                println(filename)
                FileCreate(filename)
                state.value = false
            },
            modifier = Modifier.padding(120.dp)
        ) {
            Text("ok")

        }

    }
}


@ExperimentalComposeUiApi
@Composable
fun OpenDeleteDialog(state: MutableState<Boolean>, filepath: String) {
// TODO Enter
    Dialog(onCloseRequest={state.value = false}, title = "Warning") {
        Text(
            text= "Are you sure want to delete $filepath?" ,
            modifier = Modifier.padding(60.dp, 40.dp),
            color = Color.Black,
            fontSize = 12.sp
        )
        Row {
            Button(
                onClick = {
                    FileDelete(filepath)
                    state.value = false
                },
                modifier = Modifier.padding(60.dp, 100.dp)
            ) {
                Text("Ok")

            }
            Button(
                onClick = {
                    state.value = false
                },
                modifier = Modifier.padding(60.dp, 100.dp)
            ) {
                Text("Cancel")

            }
        }

    }
}

fun FileDelete( fileName:String) {
    val file = ioFile(fileName)
    if(file.exists())
        file.deleteRecursively()
}


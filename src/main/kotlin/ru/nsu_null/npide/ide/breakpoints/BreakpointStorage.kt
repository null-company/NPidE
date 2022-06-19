package ru.nsu_null.npide.ide.breakpoints

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

@Serializable
object BreakpointStorage {

    @Serializable
    data class BreakpointStorageYaml(
        val map: HashMap<String, HashSet<Int>> = hashMapOf()
    )


    const val bpPath = "breakpoints.yml"
    var map: HashMap<String, HashSet<Int>> = loadBreakpoints()

    fun addBreakpoint(path: String, line: Int) {
        if (!map.containsKey(path)) {
            map[path] = hashSetOf()
        }
        map[path]!! += line
    }

    fun removeBreakpoint(path: String, line: Int) {
        if (map.containsKey(path) && map[path]!!.contains(line)) {
            map[path]!! -= line
        }
    }

    fun loadBreakpoints(): HashMap<String, HashSet<Int>> {
        if (File(bpPath).exists()) {
            val inputStream = FileInputStream(bpPath)
            map = try {
                Yaml.default.decodeFromStream(BreakpointStorageYaml.serializer(), inputStream).map
            } catch (e: EmptyYamlDocumentException) {
                hashMapOf()
            }
            inputStream.close()
        } else map = hashMapOf()
        return map
    }

    fun storeBreakpoints() {
        val fileOutputStream = FileOutputStream(bpPath)
        val writer = OutputStreamWriter(fileOutputStream)
        writer.write(Yaml.default.encodeToString(BreakpointStorageYaml(map)))
        writer.close()
    }
}
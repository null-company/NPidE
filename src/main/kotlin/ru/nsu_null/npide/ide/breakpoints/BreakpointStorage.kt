package ru.nsu_null.npide.ide.breakpoints

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import ru.nsu_null.npide.ide.projectchooser.ProjectChooser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

@Serializable
class BreakpointStorage(
    private val project: ProjectChooser.Project
) {
    val path = project.rootFolder.filepath + "/" + ".npide/breakpoints.yml"
    var map: HashMap<String, HashSet<Int>> = loadBreakpoints()

    @Serializable
    data class BreakpointStorageYaml(
        val map: HashMap<String, HashSet<Int>> = hashMapOf()
    )

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
        return if (File(path).exists()) {
            val inputStream = FileInputStream(path)
            val m = try {
                Yaml.default.decodeFromStream(BreakpointStorageYaml.serializer(), inputStream).map
            } catch (e: EmptyYamlDocumentException) {
                hashMapOf()
            }
            inputStream.close()
            m
        } else hashMapOf()
    }

    fun storeBreakpoints() {
        File(path).createNewFile()
        val fileOutputStream = FileOutputStream(path)
        val writer = OutputStreamWriter(fileOutputStream)
        writer.write(Yaml.default.encodeToString(BreakpointStorageYaml(map)))
        writer.close()
    }
}
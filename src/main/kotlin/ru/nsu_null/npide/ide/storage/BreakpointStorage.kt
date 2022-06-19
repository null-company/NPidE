package ru.nsu_null.npide.ide.storage

import com.charleskorn.kaml.EmptyYamlDocumentException
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileInputStream

typealias BreakPoints = MutableMap<String, MutableSet<Int>>

@Serializable
class BreakpointStorage internal constructor(private val storagePath: String) {

    @Serializable
    private data class Storage(
        val breakPoints: BreakPoints = mutableMapOf()
    )

    var breakPoints: BreakPoints = loadBreakPoints()

    /**
     * @return line-numbers for breakpoints in [fileName] or empty set if none
     */
    operator fun get(fileName: String): Set<Int> =
        breakPoints[fileName] ?: emptySet()

    operator fun set(fileName: String, breakPoints: Set<Int>) {
        this.breakPoints[fileName] = breakPoints.toMutableSet()
    }

    fun addBreakpoint(fileName: String, line: Int) {
        if (fileName !in breakPoints) {
            breakPoints[fileName] = mutableSetOf()
        }
        breakPoints[fileName]!! += line
    }

    fun removeBreakpoint(fileName: String, line: Int) {
        if (fileName in breakPoints && line in breakPoints[fileName]!!) {
            breakPoints[fileName]!! -= line
        }
    }

    private fun loadBreakPoints(): BreakPoints {
        if (File(storagePath).exists()) {
            val inputStream = FileInputStream(storagePath)
            breakPoints = try {
                Yaml.default.decodeFromStream(Storage.serializer(), inputStream).breakPoints
            } catch (e: EmptyYamlDocumentException) {
                mutableMapOf()
            }
            inputStream.close()
        } else breakPoints = mutableMapOf()
        return breakPoints
    }

    fun storeBreakpoints() {
        File(storagePath).writeText(
            Yaml.default.encodeToString(Storage(breakPoints))
        )
    }
}

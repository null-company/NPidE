package ru.nsu_null.npide.ui

import com.charleskorn.kaml.Yaml
import java.io.FileInputStream
import kotlinx.serialization.*
import java.lang.StringBuilder
class Parser(pathToConfigFile: String) {
    private val configStream = FileInputStream(pathToConfigFile)
    var result = Yaml.default.decodeFromStream(Config.serializer(), configStream)

    fun changeExt(filename: String, newExt: String?): String {
        if (newExt != null && newExt != "") {
            val arrFilename = filename.split('.').toTypedArray()
            return (arrFilename[0] + "." + newExt)
        }
        return filename
    }
    
    fun addSpaces(list: List<String>): String {
        val result = StringBuilder()
        for (element in list) {
            if (element != "") {
                result.append(element)
                    .append(" ")
            }
        }
        return result.toString()
    }

    @Serializable
    data class Config(
        val build: List<ConfigInternal>,
        val run: List<ConfigInternal>,
        val debug: List<ConfigInternal>
    )

    @Serializable
    data class ConfigInternal(
        val exec: String,
        val beforeFiles: String,
        val afterFiles: String,
        val changeExt: String?
    )
}
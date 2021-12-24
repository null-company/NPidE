package ru.nsu_null.npide.ui.yaml

import com.charleskorn.kaml.Yaml
import java.io.FileInputStream
import kotlinx.serialization.*
import java.lang.StringBuilder
class ConfigParser() {
    private val des = ConfigManager.readConfig()
    private val configStreamBuild = FileInputStream(ConfigManager.buildPath)
    private val configStreamRun = FileInputStream(ConfigManager.runPath)
    private val configStreamBDebug = FileInputStream(ConfigManager.debugPath)

    var resultBuild = Yaml.default.decodeFromStream(Config.serializer(), configStreamBuild)
    var resultRun = Yaml.default.decodeFromStream(Config.serializer(), configStreamRun)
    var resultDebug = Yaml.default.decodeFromStream(Config.serializer(), configStreamBDebug)

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
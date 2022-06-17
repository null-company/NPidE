package ru.nsu_null.npide.ide.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import ru.nsu_null.npide.ide.npide.NPIDE
import java.io.FileInputStream

class ConfigParser {
    private val configStreamBuild = FileInputStream(NPIDE.configManager.currentProjectConfig.build)
    private val configStreamRun = FileInputStream(NPIDE.configManager.currentProjectConfig.run)
    private val configStreamDebug = FileInputStream(NPIDE.configManager.currentProjectConfig.debug)

    var resultBuild = Yaml.default.decodeFromStream(Config.serializer(), configStreamBuild)
    var resultRun = Yaml.default.decodeFromStream(Config.serializer(), configStreamRun)
    var resultDebug = Yaml.default.decodeFromStream(Config.serializer(), configStreamDebug)


    @Serializable
    data class Config(
        val build: List<ConfigInternal>,
        val run: List<ConfigInternal>,
        val debug: List<ConfigInternal>
    )

    @Serializable
    data class ConfigInternal(
        val python_file: String,
        val name: String,
        val entry_point: String,
        val ext: String
    )
}
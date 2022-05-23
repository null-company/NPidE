package ru.nsu_null.npide.ui.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import ru.nsu_null.npide.ui.npide.NPIDE
import java.io.FileInputStream

@Serializable
data class DelegatesConfig(
    val build: List<DelegateDescription>,
    val run: List<DelegateDescription>,
    val debug: List<DelegateDescription>
)

@Serializable
data class DelegateDescription(
    val python_file: String,
    val name: String,
    val entry_point: String,
    val ext: String
)

fun parseConfig(): DelegatesConfig {
    val configStream = FileInputStream(NPIDE.configManager.currentProjectConfig.pathToDelegatesConfig)
    return Yaml.default.decodeFromStream(DelegatesConfig.serializer(), configStream)
}

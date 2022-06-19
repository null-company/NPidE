package ru.nsu_null.npide.ide.config

import ru.nsu_null.npide.ide.npide.NPIDE
import kotlin.reflect.KProperty

class AutoUpdatedProjectConfig internal constructor(private val configManager: ConfigManager,
                                                    projectConfig: ProjectConfig
) : ProjectConfig(
    projectConfig.projectName,
    projectConfig.entryPoint,
    projectConfig.languageDistribution,
    projectConfig.projectFiles,
) {

    private inner class AutoUpdateDelegate<T>(initialValue: T) {
        var content: T = initialValue
        operator fun getValue(
            autoUpdatedProjectConfig: AutoUpdatedProjectConfig,
            property: KProperty<*>
        ): T {
            return content
        }
        operator fun setValue(
            autoUpdatedProjectConfig: AutoUpdatedProjectConfig,
            property: KProperty<*>,
            newContent: T
        ) {
            content = newContent
            configManager.sync()
        }
    }

    override var projectName: String by AutoUpdateDelegate(super.projectName)
    override var entryPoint: String by AutoUpdateDelegate(super.entryPoint)
    override var languageDistribution: String by AutoUpdateDelegate(super.languageDistribution)
    override var projectFiles: List<String> by AutoUpdateDelegate(super.projectFiles)

    companion object {
        fun forCurrentConfigManager(projectConfig: ProjectConfig) = AutoUpdatedProjectConfig(
            NPIDE.configManager, projectConfig
        )
    }
}

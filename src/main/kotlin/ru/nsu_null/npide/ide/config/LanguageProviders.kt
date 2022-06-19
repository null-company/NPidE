package ru.nsu_null.npide.ide.config

import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager
import ru.nsu_null.npide.ide.npide.NPIDE

internal interface ConfigDependent {
    fun syncToConfig()
}

object LanguageManagerProvider : ConfigDependent {
    internal lateinit var extensionToLanguageManager: Map<String, G4LanguageManager>

    init {
        syncToConfig()
    }

    override fun syncToConfig() {
        extensionToLanguageManager = NPIDE.configManager.currentLanguageDistributionInfo
            .grammarConfigs.associate { it.sourceFileExtension to G4LanguageManager(it.sourceFileExtension) }
    }

    fun getLanguageManager(extension: String): G4LanguageManager {
        synchronized(this) {
            return extensionToLanguageManager.getOrElse(extension) {
                throw NoSuchElementException("SuchExtension is not registered by config")
            }
        }
    }
}

object ProjectSymbolProvider : ConfigDependent {
    private lateinit var languageManagerToProjectSymbolManager: Map<G4LanguageManager, ProjectSymbolManager>

    private fun ProjectSymbolManager.addFileIfNotWatched(filePath: String) {
        if (!hasFile(filePath)) {
            addFile(filePath)
        }
    }

    init {
        syncToConfig()
        for (projectSymbolManager in languageManagerToProjectSymbolManager.values) {
            for (projectFilePath in NPIDE.configManager.currentProjectConfig.projectFiles) {
                projectSymbolManager.addFileIfNotWatched(projectFilePath)
            }
        }
    }

    override fun syncToConfig() {
        languageManagerToProjectSymbolManager = LanguageManagerProvider.extensionToLanguageManager
            .values.associateWith { ProjectSymbolManager(it) }
    }

    fun getProjectSymbolManager(languageManager: G4LanguageManager): ProjectSymbolManager {
        synchronized(this) {
            return languageManagerToProjectSymbolManager.getOrElse(languageManager) {
                throw NoSuchElementException("No such language manager is registered")
            }
        }
    }
}

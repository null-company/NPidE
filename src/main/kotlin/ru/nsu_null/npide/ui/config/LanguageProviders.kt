package ru.nsu_null.npide.ui.config

import ru.nsu_null.npide.parser.generator.G4LanguageManager
import ru.nsu_null.npide.parser.translation.ProjectSymbolManager

object LanguageManagerProvider {
    internal val extensionToLanguageManager = HashMap<String, G4LanguageManager>()
    fun getLanguageManager(extension: String): G4LanguageManager {
        ConfigManager.currentProjectConfig // a workaround to make static initialization work :(
        synchronized(this) {
            return extensionToLanguageManager.getOrPut(extension) {
                ConfigManager.findGrammarConfigByExtension(extension) // throws if not found
                G4LanguageManager(extension)
            }
        }
    }
}

object ProjectSymbolProvider {
    internal val languageManagerToProjectSymbolManager = HashMap<G4LanguageManager, ProjectSymbolManager>()
    fun getProjectSymbolManager(languageManager: G4LanguageManager): ProjectSymbolManager {
        synchronized(this) {
            return languageManagerToProjectSymbolManager.getOrPut(languageManager) { ProjectSymbolManager(languageManager) }
        }
    }
}

package ru.nsu_null.npide.ide.config

import com.charleskorn.kaml.Yaml
import ru.nsu_null.npide.ide.console.logError
import ru.nsu_null.npide.ide.npide.NPIDE
import ru.nsu_null.npide.ide.projectchooser.ProjectChooser
import ru.nsu_null.npide.parser.generator.generateLexerParserFiles
import java.io.File
import java.io.FileInputStream
import java.nio.file.Paths

class ConfigManager(project: ProjectChooser.Project) {
    private val projectConfigPath: String = project.rootFolder.filepath + "/config.yaml"
    var currentProjectConfig: ProjectConfig =
        AutoUpdatedProjectConfig(
            this,
            ProjectConfig("", "", "", listOf())
        )
        set(value) {
            field = value
            sync()
        }


    init {
        loadProjectConfig()
    }

    val currentLanguageDistributionInfo: LanguageDistributionInfo =
        loadLanguageDistributionInfo()

    private fun loadLanguageDistributionInfo(): LanguageDistributionInfo {
        val languageDistributionConfig = File(currentProjectConfig.languageDistribution)
        if (!languageDistributionConfig.exists()) {
            NPIDE.console.logError("ConfigManager", "Could not find language distribution: file missing")
            return StubLanguageDistributionInfo()
        }
        return Yaml.default.decodeFromStream(
            LanguageDistributionInfo.serializer(),
            languageDistributionConfig.inputStream()
        )
    }

    internal fun sync() {
        storeProjectConfig()
        updateLanguageSystem()
    }

    private fun updateLanguageSystem() {
        if (currentLanguageDistributionInfo == null) {
            return
        }
        for (lexerPath in currentLanguageDistributionInfo.grammarConfigs.map { it.grammar }) {
            generateLexerParserFiles(
                Paths.get(lexerPath)
            )
        }
    }


    private fun storeProjectConfig() {
        val result = Yaml.default.encodeToString(
            ProjectConfig.serializer(), currentProjectConfig
        )
        File(projectConfigPath).writeText(result)
    }

    private fun loadProjectConfig() {
        val fileExists: Boolean = File(projectConfigPath).createNewFile()
        if (fileExists) {
            storeProjectConfig()
        }
        val configStream = FileInputStream(projectConfigPath)
        val result = Yaml.default.decodeFromStream(ProjectConfig.serializer(), configStream)
        currentProjectConfig = AutoUpdatedProjectConfig(this, result)
    }

    fun isProjectFile(filePath: String): Boolean = filePath in currentProjectConfig.projectFiles

    fun findGrammarConfigByExtension(extension: String): GrammarConfig {
        return currentLanguageDistributionInfo.grammarConfigs.firstOrNull {
            it.sourceFileExtension == extension
        } ?: throw NoSuchElementException()
    }
}

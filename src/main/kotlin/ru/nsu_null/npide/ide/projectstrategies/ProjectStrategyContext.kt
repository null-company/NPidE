package ru.nsu_null.npide.ide.projectstrategies

import ru.nsu_null.npide.ide.config.ProjectConfig
import ru.nsu_null.npide.ide.npide.NPIDE
import java.nio.file.Path
import kotlin.io.path.div

data class ProjectStrategyContext internal constructor(
    val projectRoot: String,
    val projectName: String,
    val entryPoint: String,
    val projectFiles: List<String>,
    val languageDistributionPath: String
) {
    companion object {
        fun fromProjectConfig(projectConfig: ProjectConfig): ProjectStrategyContext {
            val languageDistributionAbsPath = (
                    Path.of(NPIDE.currentProject!!.rootFolder.filepath) / Path.of(projectConfig.languageDistribution)
                    ).toRealPath()
            return with(projectConfig) {
                ProjectStrategyContext(
                    NPIDE.currentProject!!.rootFolder.filepath,
                    projectName,
                    entryPoint,
                    projectFiles,
                    languageDistributionAbsPath.toString()
                )
            }
        }
    }
}

typealias ExtraParameters = Map<String, String>

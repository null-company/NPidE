package ru.nsu_null.npide.ide.projectstrategies

import ru.nsu_null.npide.ide.config.ProjectConfig
import ru.nsu_null.npide.ide.npide.NPIDE

data class ProjectStrategyContext internal constructor(
    val projectRoot: String,
    val projectName: String,
    val entryPoint: String,
    val projectFiles: List<String>
) {
    companion object {
        fun fromProjectConfig(projectConfig: ProjectConfig): ProjectStrategyContext {
            return with(projectConfig) {
                ProjectStrategyContext(
                    NPIDE.currentProject!!.rootFolder.filepath,
                    projectName,
                    entryPoint,
                    projectFiles
                )
            }
        }
    }
}

typealias ExtraParameters = Map<String, String>

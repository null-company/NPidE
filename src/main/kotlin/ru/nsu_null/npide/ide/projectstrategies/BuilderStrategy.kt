package ru.nsu_null.npide.ide.projectstrategies

import ru.nsu_null.npide.ide.console.Logger
import ru.nsu_null.npide.ide.console.process.ConsoleProcess
import ru.nsu_null.npide.ide.storage.BreakPoints
import ru.nsu_null.npide.ide.storage.DirtyFlags

interface BuilderStrategy: ConsoleProcess {
    /**
     * Builds current project
     * @param enableDebugInfo true iff should build with debugging info
     * @param strategyContext contains information about the project,
     * @param extraConfiguration key-value configuration provided in the language distribution
     * @param breakPoints which are used to generate debug info
     * @param dirtyFlags maps file name to its 'dirtiness'
     * @param logger a [Logger] which can be used to log events or report errors
     */
    fun build(
        enableDebugInfo: Boolean,
        strategyContext: ProjectStrategyContext,
        extraConfiguration: ExtraConfiguration,
        breakPoints: BreakPoints,
        dirtyFlags: DirtyFlags,
        logger: Logger
    )
}

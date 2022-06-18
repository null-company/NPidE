package ru.nsu_null.npide.ide.projectstrategies

import ru.nsu_null.npide.ide.console.Logger
import ru.nsu_null.npide.ide.console.process.ConsoleProcess

interface RunnerStrategy: ConsoleProcess {
    /**
     * Runs the already built project
     * @param strategyContext contains information about the project,
     * @param extraConfiguration key-value configuration provided in the language distribution
     * @param logger a [Logger] which can be used to log events or report errors
     */
    fun run(
        strategyContext: ProjectStrategyContext,
        extraConfiguration: ExtraConfiguration,
        logger: Logger
    )
}

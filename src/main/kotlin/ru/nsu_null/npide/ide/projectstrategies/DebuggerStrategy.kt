package ru.nsu_null.npide.ide.projectstrategies

import ru.nsu_null.npide.ide.console.Logger
import ru.nsu_null.npide.ide.console.process.ConsoleProcess
import ru.nsu_null.npide.ide.storage.BreakPoints

interface DebuggerStrategy: ConsoleProcess {

    /**
     * This set should describe what this debugger is able to do
     */
    val abilities: Set<DebuggerAbility>

    /**
     * Builds current project
     * @param strategyContext contains information about the project,
     * @param extraParameters key-value configuration provided in the language distribution
     * @param breakPoints which are used to generate debug info
     * @param logger a [Logger] which can be used to log events or report errors
     */
    fun debug(
        strategyContext: ProjectStrategyContext,
        extraParameters: ExtraParameters,
        breakPoints: BreakPoints,
        logger: Logger
    )

    /**
     * Performs a step in the debugger
     * @throws IllegalStateException if not running
     * @throws UnsupportedOperationException if this [DebuggerStrategy] does not support stepping
     */
    fun step()

    /**
     * @throws IllegalStateException if not running
     * @throws UnsupportedOperationException if this [DebuggerStrategy] does not support continue
     */
    fun cont()

    /**
     * Send some generic command
     */
    fun sendGeneralCommand(command: String)

    /**
     * @return a map that associates variable names with their values
     * @throws UnsupportedOperationException if this [DebuggerStrategy] does not support watches as map
     */
    fun getWatches(): Map<String, String>

    /**
     * A less strict version of [getWatches]
     * @return string which represents the state of currently available variables
     * @throws UnsupportedOperationException if this [DebuggerStrategy] does not support watches as string
     */
    fun getWatchesAsString(): String
}

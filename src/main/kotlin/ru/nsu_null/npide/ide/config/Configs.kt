package ru.nsu_null.npide.ide.config

import kotlinx.serialization.Serializable
import ru.nsu_null.npide.ide.projectstrategies.ExtraConfiguration

@Serializable
open class ProjectConfig(
    open var projectName: String,
    open var entryPoint: String,
    open var languageDistribution: String,
    open var projectFiles: List<String>,
)

@Serializable
data class LanguageDistributionInfo(
    val buildStrategy: StrategyInfo,
    val runStrategy: StrategyInfo,
    val debugStrategy: StrategyInfo,
    var grammarConfigs: List<GrammarConfig>
)

@Serializable
data class StrategyInfo(
    val strategyClass: String,
    val extraConfiguration: ExtraConfiguration
)

@Suppress("FunctionName")
internal fun StubLanguageDistributionInfo() : LanguageDistributionInfo = LanguageDistributionInfo(
    StubStrategyInfo(), StubStrategyInfo(), StubStrategyInfo(), listOf()
)

@Suppress("FunctionName")
internal fun StubStrategyInfo() : StrategyInfo = StrategyInfo(
    "", mapOf()
)

@Serializable
data class GrammarConfig(
    val sourceFileExtension: String,
    val grammar: String,
    val syntaxHighlighter: String,
)

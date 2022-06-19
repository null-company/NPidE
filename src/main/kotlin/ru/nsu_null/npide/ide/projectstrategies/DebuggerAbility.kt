package ru.nsu_null.npide.ide.projectstrategies

sealed class DebuggerAbility {
    object Step : DebuggerAbility()
    object Continue : DebuggerAbility()
    object WatchesMap : DebuggerAbility()
    object WatchesString : DebuggerAbility()
}

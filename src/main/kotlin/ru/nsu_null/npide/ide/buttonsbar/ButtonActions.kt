package ru.nsu_null.npide.ide.buttonsbar

import ru.nsu_null.npide.ide.npide.NPIDE

fun build() {
    // TODO fix dirty flag checking
    NPIDE.buildCurrentProject(enableDebugInfo = false)
}

fun run() {
    NPIDE.buildCurrentProject(enableDebugInfo = false)
    NPIDE.runCurrentProject()
}

fun debug() {
    NPIDE.debugCurrentProject()
}

fun step() {
    NPIDE.debuggerStep()
}

fun cont() {
    NPIDE.debuggerContinue()
}

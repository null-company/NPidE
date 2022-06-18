package ru.nsu_null.npide.ide.util

import kotlin.reflect.KFunction0
import kotlin.reflect.KProperty

class MethodDelegator<T>(private val delegate: KFunction0<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        delegate()
}

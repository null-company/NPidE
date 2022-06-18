package ru.nsu_null.npide.ide.console

interface Logger {
    fun log(who: String, message: String, messageType: Console.MessageType)
}

class ConsoleLogger(val console: Console) : Logger {
    override fun log(who: String, message: String, messageType: Console.MessageType) =
        console.log(who, message, messageType)
}

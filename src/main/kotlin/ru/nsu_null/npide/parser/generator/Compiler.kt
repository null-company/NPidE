package ru.nsu_null.npide.parser.generator


fun compileGeneratedClasses(directory: String) {
    println("compiled dir: $directory")
    Runtime.getRuntime().exec("javac -sourcepath $directory $directory/*.java")
}
package ru.nsu_null.npide.platform

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FilenameFilter
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets

interface File {
    val name: String
    val filepath: String
    val parentPath: String
    val isDirectory: Boolean
    val children: List<File>
    val hasChildren: Boolean

    fun getContents(scope: CoroutineScope): MutableState<String>
}

val HomeFolder: File get() = java.io.File(System.getProperty("user.home")).toProjectFile()

fun java.io.File.toProjectFile(): File = object : File {
    override val name: String get() = this@toProjectFile.name

    override val filepath: String get() = this@toProjectFile.absolutePath

    override val parentPath: String get() = this@toProjectFile.parent


    override val isDirectory: Boolean get() = this@toProjectFile.isDirectory

    override val children: List<File>
        get() = this@toProjectFile
            .listFiles(FilenameFilter { _, name -> !name.startsWith(".")})
            .orEmpty()
            .map { it.toProjectFile() }

    override val hasChildren: Boolean
        get() = isDirectory && listFiles()?.size ?: 0 > 0


    override fun getContents(scope: CoroutineScope): MutableState<String> {
        var byteBufferSize: Int
        println("Trying to access ${this.name}")
        val byteBuffer = RandomAccessFile(this@toProjectFile, "rw").use { file ->
            byteBufferSize = file.length().toInt()
            file.channel
                .map(FileChannel.MapMode.READ_WRITE, 0, file.length())
        }

        val fileContents = mutableStateOf("")

        scope.launch(Dispatchers.IO) {
            while (true) {
                delay(5000)
                fileContents.value = StandardCharsets.UTF_8.decode(byteBuffer).toString()
            }
        }

        fileContents.value = StandardCharsets.UTF_8.decode(byteBuffer).toString()

        return fileContents
    }
}

private fun java.io.File.readLinePositions(
    starts: IntList
) {
    require(length() <= Int.MAX_VALUE) {
        "Files with size over ${Int.MAX_VALUE} aren't supported"
    }

    val averageLineLength = 200
    starts.clear(length().toInt() / averageLineLength)

    try {
        FileInputStream(this@readLinePositions).use {
            val channel = it.channel
            val ib = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, channel.size()
            )
            var isBeginOfLine = true
            var position = 0L
            while (ib.hasRemaining()) {
                val byte = ib.get()
                if (isBeginOfLine) {
                    starts.add(position.toInt())
                }
                isBeginOfLine = byte.toInt().toChar() == '\n'
                position++
            }
            channel.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        starts.clear(1)
        starts.add(0)
    }

    starts.compact()
}

/**
 * Compact version of List<Int> (without unboxing Int and using IntArray under the hood)
 */
private class IntList(initialCapacity: Int = 16) {
    @Volatile
    private var array = IntArray(initialCapacity)

    @Volatile
    var size: Int = 0
        private set

    fun clear(capacity: Int) {
        array = IntArray(capacity)
        size = 0
    }

    fun add(value: Int) {
        if (size == array.size) {
            doubleCapacity()
        }
        array[size++] = value
    }

    operator fun get(index: Int) = array[index]

    private fun doubleCapacity() {
        val newArray = IntArray(array.size * 2 + 1)
        System.arraycopy(array, 0, newArray, 0, size)
        array = newArray
    }

    fun compact() {
        array = array.copyOfRange(0, size)
    }
}
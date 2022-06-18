package ru.nsu_null.npide.ide.storage

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileInputStream

typealias DirtyFlags = MutableMap<String, Boolean>

class DirtyFlagsStorage internal constructor(private val storagePath: String) {
    @Serializable
    private data class Storage(
        val fileNameToDirtiness: MutableMap<String, Boolean>
    )

    private var storage = Storage(mutableMapOf())

    val dirtyFlags: DirtyFlags
        get() = storage.fileNameToDirtiness

    init {
        load()
    }

    private fun load() {
        if (File(storagePath).exists()) {
            store()
        }
        storage = Yaml.default.decodeFromStream(
            Storage.serializer(), FileInputStream(storagePath)
        )
    }

    private fun store() {
        File(storagePath).writeText(
            Yaml.default.encodeToString(Storage.serializer(), storage)
        )
    }

    /**
     * Check a file dirtiness
     *
     * @throws [NoSuchElementException] if the fileName is unknown
     */
    operator fun get(fileName: String): Boolean =
        storage.fileNameToDirtiness[fileName] ?: throw NoSuchElementException("No such file")

    operator fun set(fileName: String, isDirty: Boolean) {
        storage.fileNameToDirtiness[fileName] = isDirty
        store()
    }
}

package ru.nsu_null.npide.ui.yaml

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import java.io.File
import java.io.FileInputStream

object ProjectFile{
    const val projectFilePath:String = "config.yaml"
    var buildPath:String = ""
    var runPath:String = ""
    var debugPath:String = ""


    @Serializable
    data class ProjectFileConfigFile(
        val build: String,
        val run: String,
        val debug: String,

        )
    fun serialize(){
        val result = Yaml.default.encodeToString(
            ProjectFileConfigFile.serializer(), ProjectFileConfigFile(
                buildPath,
                runPath,
                debugPath,
            )
        )
        File(projectFilePath).writeText(result)
    }
    fun deserialize(){
        val fileIsExist :Boolean = File(projectFilePath).createNewFile()
        if(fileIsExist){
            serialize()
        }
        val configStream = FileInputStream(projectFilePath)
        val result = Yaml.default.decodeFromStream(ProjectFileConfigFile.serializer(), configStream)
        buildPath = result.build
        runPath = result.run
        debugPath = result.debug

    }

}
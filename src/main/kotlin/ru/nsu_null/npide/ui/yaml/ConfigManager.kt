package ru.nsu_null.npide.ui.yaml

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import java.io.File
import java.io.FileInputStream

object ConfigManager{
    const val projectFilePath:String = "config.yaml"
    var buildPath:String = ""
    var runPath:String = ""
    var debugPath:String = ""
    private var fileInformation: HashMap<String?, Boolean?>?  = hashMapOf()

    @Serializable
    data class ProjectFileConfigFile(
        val build: String,
        val run: String,
        val debug: String,
        val editFileInformation: HashMap<String?, Boolean?>?
        )

    fun storeConfig(){
        val result = Yaml.default.encodeToString(
            ProjectFileConfigFile.serializer(), ProjectFileConfigFile(
                buildPath,
                runPath,
                debugPath,
                fileInformation
            )
        )
        File(projectFilePath).writeText(result)
    }

    fun readConfig(){
        val fileIsExist :Boolean = File(projectFilePath).createNewFile()
        if(fileIsExist){
            storeConfig()
        }
        val configStream = FileInputStream(projectFilePath)
        val result = Yaml.default.decodeFromStream(ProjectFileConfigFile.serializer(), configStream)
        buildPath = result.build
        runPath = result.run
        debugPath = result.debug
        fileInformation = result.editFileInformation!!
    }

    fun setFileBuilt(file:String, isBuilt:Boolean){
        fileInformation?.put(file, isBuilt)
        val result = Yaml.default.encodeToString(
            ProjectFileConfigFile.serializer(), ProjectFileConfigFile(
                buildPath,
                runPath,
                debugPath,
                fileInformation
            )
        )
        File(projectFilePath).writeText(result)
    }
    fun readFileBuilt(file:String): Boolean? {
        val configStream = FileInputStream(projectFilePath)
        val result = Yaml.default.decodeFromStream(ProjectFileConfigFile.serializer(), configStream)
        fileInformation = result.editFileInformation!!
        if(fileInformation!![file] == null)
            return false
        return fileInformation!![file]

    }

}
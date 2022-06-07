package ru.nsu_null.npide.ide.projectchooser

import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import ru.nsu_null.npide.ide.platform.File
import ru.nsu_null.npide.ide.platform.toProjectFile

class ProjectChooser(private val repositoryManager: ProjectsRepositoryManager) {

    interface ProjectsRepositoryManager {
        val existingProjects: List<Project>
        fun addNewProject(newProject: Project)
        fun deleteProject(index: Int)
    }

    @Serializable
    data class Project(
        val rootFolder: File
    ) {
        constructor(rootPath: String) : this(java.io.File(rootPath).toProjectFile())
    }

    val availableProjects = mutableStateOf(repositoryManager.existingProjects)

    fun addProject(project: Project) {
        availableProjects.value += project
        repositoryManager.addNewProject(project)
    }

    fun deleteProject(project: Project) {
        val index = availableProjects.value.indexOf(project)
        availableProjects.value -= project
        repositoryManager.deleteProject(index)
    }
}

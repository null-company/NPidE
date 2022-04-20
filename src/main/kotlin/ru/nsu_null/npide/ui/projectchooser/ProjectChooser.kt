package ru.nsu_null.npide.ui.projectchooser

import kotlinx.serialization.Serializable
import ru.nsu_null.npide.platform.File

class ProjectChooser(private val repositoryManager: ProjectsRepositoryManager) {

    interface ProjectsRepositoryManager {
        val existingProjects: List<Project>
        fun addNewProject(newProject: Project)
    }

    @Serializable
    data class Project(
        val rootFolder: File
    )

    var availableProjects: List<Project> = repositoryManager.existingProjects
}

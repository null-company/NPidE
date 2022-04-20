package ru.nsu_null.npide.ui.projectchooser

import ru.nsu_null.npide.platform.HomeFolder
import ru.nsu_null.npide.platform.toProjectFile
import java.nio.file.Files
import java.nio.file.Path

class DiskHomeDirectoryRepositoryManager : ProjectChooser.ProjectsRepositoryManager {

    companion object {
        private val commonRepositoryPath = "${HomeFolder.filepath}/.config/npide/" // todo move to other place for it is shared
        private val projectsRepositoryFilePath = commonRepositoryPath + "projects"
    }

    init {
        Files.createDirectories(Path.of(commonRepositoryPath)) // todo move this aswell
        java.io.File(projectsRepositoryFilePath).createNewFile()
    }

    override val existingProjects: List<ProjectChooser.Project>
        get() = java.io.File(projectsRepositoryFilePath).readLines().map {
            ProjectChooser.Project(java.io.File(it).toProjectFile())
        }

    override fun addNewProject(newProject: ProjectChooser.Project) {
        TODO("Not yet implemented")
    }
}

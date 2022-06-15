package ru.nsu_null.npide.ide.projectchooser

import ru.nsu_null.npide.ide.platform.HomeFolder
import ru.nsu_null.npide.ide.platform.toProjectFile
import java.nio.file.Files
import java.nio.file.Path

class DiskHomeDirectoryRepositoryManager : ProjectChooser.ProjectsRepositoryManager {

    companion object {
        private val commonRepositoryPath =
            "${HomeFolder.filepath}/.config/npide/" // todo move to other place for it is shared
        private val projectsRepositoryFilePath = commonRepositoryPath + "projects"
    }

    init {
        Files.createDirectories(Path.of(commonRepositoryPath)) // todo move this aswell
        java.io.File(projectsRepositoryFilePath).createNewFile()
    }

    override var existingProjects: List<ProjectChooser.Project>
        get() = java.io.File(projectsRepositoryFilePath).readLines().filter { it.isNotBlank() }.map {
            ProjectChooser.Project(java.io.File(it).toProjectFile())
        }
        set(projects) {
            java.io.File(projectsRepositoryFilePath).writeText(
                (projects.map { it.rootFolder.filepath }).joinToString("\n")
            )
        }

    override fun addNewProject(newProject: ProjectChooser.Project) {
        existingProjects = existingProjects + newProject
    }

    override fun deleteProject(index: Int) {
        existingProjects = existingProjects.toMutableList().also { it.removeAt(index) }
    }
}

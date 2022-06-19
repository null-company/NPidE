package ru.nsu_null.npide.ide.storage

import ru.nsu_null.npide.ide.projectchooser.ProjectChooser
import java.io.File

class ProjectStorage(project: ProjectChooser.Project) {

    private val projectStorageDirectory = File(project.rootFolder.filepath + "/.npide")

    init {
        if (!projectStorageDirectory.exists()) {
            projectStorageDirectory.mkdirs()
        }
    }

    val breakpointStorage = BreakpointStorage(
        projectStorageDirectory.absolutePath + "/breakpoint-storage.yaml"
    )

    val dirtyFlagsStorage: DirtyFlagsStorage = DirtyFlagsStorage(
        projectStorageDirectory.absolutePath + "/dirty-flags.yaml"
    )

}

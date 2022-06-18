package ru.nsu_null.npide.ide.storage

import ru.nsu_null.npide.ide.projectchooser.ProjectChooser

class ProjectStorage(project: ProjectChooser.Project) {

    val breakpointStorage = BreakpointStorage(
        project.rootFolder.filepath + "/.npide/breakpoint-storage.yaml"
    )

    val dirtyFlagsStorage: DirtyFlagsStorage = DirtyFlagsStorage(
        project.rootFolder.filepath + "/.npide/dirty-flags.yaml"
    )

}

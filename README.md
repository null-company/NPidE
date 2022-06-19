<div align="center">
  <br>
  <img alt="NPidE logo" src="src/main/resources/npide.png">
  <h1>🍇 NPidE 🍇</h1>
  <strong>A highly configurable minimalistic IDE for language developers</strong>
</div>
<br>
<p align="center">
    <a href="https://github.com/JetBrains/kotlin">
      <img src="https://img.shields.io/badge/kotlin-1.6.10-violet?logo=kotlin" alt="kotlin" style="max-width: 100%;">
    </a>
    <a href="https://github.com/gradle/gradle">
      <img src="https://img.shields.io/badge/gradle-7.1.0-02303A?logo=gradle" alt="gradle" style="max-width: 100%;">
    </a>
    <a href="https://github.com/JetBrains/compose-jb">
      <img src="https://img.shields.io/static/v1?&message=1.1.1&color=4285F4&logo=Jetpack+Compose&logoColor=4285F4&label=Compose" alt="Compose" style="max-width: 100%;">
    </a>
    <a href="https://github.com/antlr/antlr4">
      <img src="https://img.shields.io/badge/antlr-4.10.1-brightgreen" alt="Compliance" style="max-width: 100%;">
    </a>
</p>
<p align="center">
  <a href="#❔-why-npide">Why NPidE?</a> •
  <a href="#📙-supported-languages">Supported languages</a> •
  <a href="#⚙️-installation">Installation</a> •
  <a href="#🔢-functionality">Functionality</a> •
  <a href="#➕-how-to-add-new-language">How to add new language?</a> •
  <a href="#🔧-architecture">Architecture</a> •
  <a href="#⚖️-license">License</a> •
  <a href="#📕-authors">Authors</a> •
  <a href="#🏥-support">Support</a> •
  <a href="#📆-plans">Plans</a>

</p>

# ❔ Why NPidE?
The main feature of this IDE is its customizability. You can add supported languages, project types and so on in a declarative style using config files

# 📙 Supported languages
![AssemblyScript](https://img.shields.io/static/v1?style=for-the-badge&message=Cocas%20Assembly&color=007AAC&logo=AssemblyScript&logoColor=FFFFFF&label=)
![Clojure](https://img.shields.io/static/v1?style=for-the-badge&message=Clojure&color=5881D8&logo=Clojure&logoColor=FFFFFF&label=)
# ⚙️ Installation

1. Clone repository
    ```console
    git clone https://github.com/nsu-null/NPidE
    ```
2. Create JAR
   ```console
   gradle jar   # or ./gradlew jar 
   ```
3. Execute JAR
   ```console
   java -jar path/to/NPidE-<version>.jar
   ```

  OR you can just download NPidE fron release page

# 🔢 Functionality
* Create/remove projects
* Configurate compilation/execution/debug for languages
* Configurate grammar and highlight syntax for languages
* Open/edit/close/create/delete/save/rename/.. files
* Build/Run/Debug project with supported languages
* Add/remove breakpoints for debugging
* Watch system variables in debug mode
* Step system in debug mode
* Terminal window in NPidE

# ➕ How to add new language?
simple

# 🔧 Architecture

## Modules

### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/parser">🔖 Parser</a> 
  This module is responsible for analyzing the files being edited and create internal structure for describing this ones
  - translation - creates symbol tables and so on
  - generator - generates parser and lexer files based on provided grammar
  - compose_support - allows to connect highlighting to our editing text area
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/editor">✍ Editor</a> 
  - Editor - represents a state of a file editor
  - Editors - controls currently open Editors
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/console">💻 Console</a>
  - Console - responsible for getting output from build/run/debug
  - ConsoleView - responsible for drawing the aforementioned output
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/buttonsbar">🖱 ButtonsBar</a>
  - ButtonsBar - responsible for drawing bar for buttons 
  - ButtonActions - responsible for handling button clicks
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/menubar">🎛 MenuBar</a>
  - MenuBar - responsible for drawing bar for menu buttons(top panel) 
  - ConfigDialog - responsible for handling menu button clicks and open dialog for project configuration
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/config">🔗 Config</a>
  responsible for storing and reading configuration from YAML-file
  - Config Manager -  manage project configuration file 
  - Config Parser - parse config file with configuration about build/run/debug
  - LanguageProviders
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/filetree">📁 FileTree</a>
  - FileDialog
  - FileTree 
  - FileTreeView
  - ExpandableFile
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/projectchooser">🏗 ProjectChooser</a>
  - ProjectChooser - responsible for handling choosing projects
  - ProjectChooserView - responsible for drawing project lists 
  - DiskHomeDirectoryRepositoryManager - manage project files on disk
### <a href="https://github.com/nsu-null/NPidE/tree/main/src/main/kotlin/ru/nsu_null/npide/ide/breakpoints">🔴 Breakpoints</a>
  - BreakpointStorage - responsible for adding/removing breakpoints

## Patterns
- Singleton
- Proxy
- Observer
- Delegation
- Object pool
- Factory
- Strategy

# ⚖️ License
[![License-MIT](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://tldrlegal.com/license/mit-license)

# 📕 Authors
| <img src="https://avatars.githubusercontent.com/u/42515597?v=4" width="100"/>| <img src="https://avatars.githubusercontent.com/u/71331317?v=4" width="100"/>  | <img src="https://avatars.githubusercontent.com/u/37692980?v=4" width="100"/>  | <img src="https://avatars.githubusercontent.com/u/33481844?v=4" width="100"/>  |
| ---------------------------------------------------------------------------- | ------------------------------------------------------------------------------ | ------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
|                                   **Brek Roman**                            |                                      **Vasilev Pavel**                         |                              **Patrushev Borya**                        |                                 **Tarasov Artёm**                              |

# 🏥 Support

# 📆 Plans
* Pair programming
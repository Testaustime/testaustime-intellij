package com.github.developerfromjokela.testaustimeintellij.services

import com.intellij.openapi.project.Project
import com.github.developerfromjokela.testaustimeintellij.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

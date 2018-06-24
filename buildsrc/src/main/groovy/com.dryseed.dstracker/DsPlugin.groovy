package com.dryseed.dstracker

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class DsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println "================== DsPlugin Start ======================="

        //task耗时监听
        project.gradle.addListener(new TaskTimeCostListener())

        //遍历class文件和jar文件，在这里可以进行class文件asm文件替换
        BaseExtension android = project.extensions.getByType(BaseExtension)
        DsTransform transform = new DsTransform(project)
        android.registerTransform(transform)
    }

}
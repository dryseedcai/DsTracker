package com.dryseed.dstracker
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


public class DsPlugin implements Plugin<Project> {

    void apply(Project project) {
        System.out.println("------------------开始----------------------");
        System.out.println("这是我们的自定义插件!");
        System.out.println("------------------结束----------------------->");

        //def android = project.extensions.getByType(AppExtension)
        //注册一个Transform
//        def classTransform = new DsTransform(project);
//        android.registerTransform(classTransform);

//        project.extensions.create('combuild', ComExtension)
//        DsTransform transform = new DsTransform(project)
//        project.android.registerTransform(transform)

//        BaseExtension android = project.extensions.getByType(BaseExtension)
//        DsTransform transform = new DsTransform(project)
//        android.registerTransform(transform)

        def android = project.extensions.getByType(AppExtension)
        //注册一个Transform
        def classTransform = new DsTransform(project);
        android.registerTransform(classTransform);
        System.out.println("dsdsds")
    }
}
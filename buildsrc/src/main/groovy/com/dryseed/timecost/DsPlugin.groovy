package com.dryseed.timecost

import com.android.build.api.transform.QualifiedContent
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.android.builder.Version
import com.dryseed.timecost.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author caiminming
 */
class DsPlugin implements Plugin<Project> {
    private static final Set<QualifiedContent.Scope> SCOPES = new HashSet<>()
    static {
        SCOPES.add(QualifiedContent.Scope.PROJECT)
        SCOPES.add(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS)
        SCOPES.add(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        SCOPES.add(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
    }

    static final boolean ABOVE_ANDROID_GRADLE_PLUGIN_3
    static {
        String aVersion = Version.ANDROID_GRADLE_PLUGIN_VERSION.trim()
        char[] aVersionChars = aVersion.chars
        if (aVersionChars.length == 0) {
            ABOVE_ANDROID_GRADLE_PLUGIN_3 = false
        } else {
            char firstChar = aVersion.charAt(0)
            if (aVersionChars.length == 1) {
                ABOVE_ANDROID_GRADLE_PLUGIN_3 = firstChar >= '3'
            } else {
                ABOVE_ANDROID_GRADLE_PLUGIN_3 = aVersion.charAt(1) != '.' || firstChar >= '3'
            }
        }
    }

    private File mBuildDir

    @Override
    void apply(Project project) {
        Log.info("================== DsPlugin Start =======================")

        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)

        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }

        if (ABOVE_ANDROID_GRADLE_PLUGIN_3) {
            //PROJECT_LOCAL_DEPS, SUB_PROJECTS_LOCAL_DEPS deprecated, replaced by EXTERNAL_LIBRARIES
            SCOPES.remove(QualifiedContent.Scope.PROJECT_LOCAL_DEPS)
            SCOPES.remove(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
        }

        if (hasLib) {
            //Transforms with scopes '[SUB_PROJECTS, SUB_PROJECTS_LOCAL_DEPS, EXTERNAL_LIBRARIES]' cannot be applied to library projects.
            SCOPES.remove(QualifiedContent.Scope.SUB_PROJECTS)
            SCOPES.remove(QualifiedContent.Scope.SUB_PROJECTS_LOCAL_DEPS)
            SCOPES.remove(QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        }

        project.extensions.create('timeCostConfig', DsPluginParams)
        mBuildDir = new File(project.getBuildDir(), "TimeCost")

        //遍历class文件和jar文件，在这里可以进行class文件asm文件替换
        BaseExtension android = project.extensions.getByType(BaseExtension)
        DsTransform transform = new DsTransform(project, mBuildDir, SCOPES)
        android.registerTransform(transform)

        Log.info(String.format(
                "timeCostConfig : isDebug = %b | watchTaskTimeCost = %b",
                project.timeCostConfig.isDebug,
                project.timeCostConfig.watchTaskTimeCost
        ))

        project.afterEvaluate {
            Log.setIsDebug(project.timeCostConfig.isDebug)
            if (project.timeCostConfig.watchTaskTimeCost) {
                //task耗时监听
                project.gradle.addListener(new TaskTimeCostListener())
                Log.info("watchTaskTimeCost enabled")
            } else {
                Log.info("watchTaskTimeCost disabled")
            }
        }
    }

}
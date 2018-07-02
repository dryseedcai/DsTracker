package com.dryseed.dstracker

import com.android.build.gradle.BaseExtension
import com.dryseed.dstracker.utils.Log
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author caiminming
 */
class DsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        Log.info("================== DsPlugin Start =======================")

        project.extensions.create('dsTrackerConfig', DsPluginParams)

        //遍历class文件和jar文件，在这里可以进行class文件asm文件替换
        BaseExtension android = project.extensions.getByType(BaseExtension)
        DsTransform transform = new DsTransform(project)
        android.registerTransform(transform)

        Log.info(String.format(
                "dsTrackerConfig : isDebug = %b | watchTaskTimeCost = %b",
                project.dsTrackerConfig.isDebug,
                project.dsTrackerConfig.watchTaskTimeCost
        ))

        project.afterEvaluate {
            Log.setIsDebug(project.dsTrackerConfig.isDebug)
            if (project.dsTrackerConfig.watchTaskTimeCost) {
                //task耗时监听
                project.gradle.addListener(new TaskTimeCostListener())
                Log.info("watchTaskTimeCost enabled")
            } else {
                Log.info("watchTaskTimeCost disabled")
            }
        }
    }

}
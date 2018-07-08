package com.dryseed.timecost

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState

/**
 * @author caiminming
 */
class TaskTimeCostListener implements TaskExecutionListener, BuildListener {

    private Clock clock
    private times = []

    @Override
    void buildStarted(Gradle gradle) {

    }

    @Override
    void settingsEvaluated(Settings settings) {

    }

    @Override
    void projectsLoaded(Gradle gradle) {

    }

    @Override
    void projectsEvaluated(Gradle gradle) {

    }

    @Override
    void buildFinished(BuildResult buildResult) {
        println "Task build Finish,Total time:"
        for (time in times) {
            if (time[0] < 1000) {
                continue
            }
            printf "%7sms  %s\n", time
        }
    }

    @Override
    void beforeExecute(Task task) {
        clock = new Clock(new Date().getTime())
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        def ms = clock.getTimeInMs()
        times.add([ms, task.path])
    }

}

class Clock {
    long startTimeInMs

    Clock(long startTimeInMs) {
        this.startTimeInMs = startTimeInMs
    }

    long getTimeInMs() {
        return System.currentTimeMillis() - startTimeInMs
    }
}
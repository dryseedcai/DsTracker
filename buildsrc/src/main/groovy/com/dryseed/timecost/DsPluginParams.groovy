package com.dryseed.timecost

import org.gradle.api.Action
import org.gradle.internal.hash.HashUtil

/**
 * @author caiminming
 */
class DsPluginParams {
    boolean isDebug = true
    boolean watchTaskTimeCost = true
    HashSet<String> whitePackageList = []
    HashSet<String> blackPackageList = []
    boolean autoInject = false
    boolean jarInject = false
    Scope scope = new Scope()

    void scope(Action<Scope> action) {
        action.execute(scope)
    }

    Scope getScope() {
        return scope
    }

    class Scope {
        boolean project = true
        boolean projectLocalDep = false
        boolean subProject = true
        boolean subProjectLocalDep = false
        boolean externalLibraries = false

        void project(boolean enable) {
            project = enable
        }

        void projectLocalDep(boolean enable) {
            projectLocalDep = enable
        }

        void subProject(boolean enable) {
            subProject = enable
        }

        void subProjectLocalDep(boolean enable) {
            subProjectLocalDep = enable
        }

        void externalLibraries(boolean enable) {
            externalLibraries = enable
        }

        @Override
        public String toString() {
            return "Scope{" +
                    "project=" + project +
                    ", projectLocalDep=" + projectLocalDep +
                    ", subProject=" + subProject +
                    ", subProjectLocalDep=" + subProjectLocalDep +
                    ", externalLibraries=" + externalLibraries +
                    '}';
        }
    }
}
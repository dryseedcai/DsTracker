package com.dryseed.timecost

/**
 * @author caiminming
 */
class DsPluginParams {
    boolean isDebug = true
    boolean watchTaskTimeCost = true
    HashSet<String> whitePackageList = []
    HashSet<String> blackPackageList = []
    boolean autoInject = false
}
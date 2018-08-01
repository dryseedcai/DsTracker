## TODO LIST
* 耗时所在线程的过滤机制 Completed
* 提供代码插桩阶段的白名单功能 Completed
* 提供无注解自动插桩 Completed
* Release No Operation Completed
* 超时UI界面 Completed
* 耗时性能排序 Completed
* Notification开关 Completed
* 注入jar包开关 Completed
* 上传maven Completed
* 耗时统计优化（setStartTime/setEndTime）Completed
* 耗时区间配置
* 统计时间设定
* json导入插桩配置
* 生成可视化报告
* gradle plugin 插件优化
* 结合BlockCanary


## HELP

* 引入方法
    * project/build.gradle 中引入
    ```
    repositories {
        // TODO: upload to maven or jcenter
        maven {
            url uri('C:/repo')
        }
    }

    buildscript {
        dependencies {
            classpath "com.dryseed.timecost.gradle:buildsrc:1.+"
        }
    }

    allprojects {
        repositories {
            // TODO: upload to maven or jcenter
            maven {
                url uri('C:/repo')
            }
        }
    }
    ```
    * app/build.gradle 中引入
    ```
     apply plugin: 'com.dryseed.timecost.gradle.dsplugin'

     dependencies {
        compile 'com.dryseed.timecost:timecostimp:1.+'
     }

     //timecost
     timeCostConfig {
         //this flag will decide whether the log of the time-cost plugin be printed or not, default value is true
         isDebug = true
         //this is a kit feature of the plugin, set it true to see the time consume of this build
         watchTaskTimeCost = false
         // the plugin will only inject package / class defined in whitePackageList
         whitePackageList = ['org.qiyi.video', 'com.qiyi.video', 'org.qiyi.android', 'org.qiyi.card']
         // thp plugin will not inject package, white list has a higher priority than white list
         blackPackageList = []
         // the plugin will inject automatically based on whitePackageList
         autoInject = true
         // this flag will decide whether the plugin will inject the jars, false by default
         jarInject = false
         scope {
             project true //inject app project, default true
             projectLocalDep false //inject app libs(eg: .jar), default false
             subProject true //inject subProject of app project(eg: module), default true
             subProjectLocalDep false //inject subProject libs, default false
             externalLibraries false //inject external libs(eg: .aar), default false
         }
     }
    ```
    * Application 中初始化TimeCost
    ```
    TimeCostCanary.install(getApplication()).config(
            new TimeCostConfig.Builder()
                    .setExceedMilliTime(1000L)
                    .setThreadExceedMilliTime(1000L)
                    .setMonitorOnlyMainThread(true)
                    .setShowDetailUI(true)
                    .build()
    );
    ```

* 耗时所在线程的过滤机制
    * 在注解上设置，优先级高于全局动态设置
    ```
    @TimeCost(monitorOnlyMainThread = true)
    ```
    * 全局动态配置
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setMonitorOnlyMainThread(true)
                    .build()
    );
    ```

* 代码插桩阶段的白名单/黑名单功能
    ```
    timeCostConfig {
        whitePackageList = ['com.dryseed.timecost']
        blackPackageList = []
    }
    ```

* 无注解自动插桩
    ```
    timeCostConfig {
        autoInject = true
    }
    ```

* Release No Operation
    ```
    debugCompile project(':timecostimp')
    releaseCompile project(':timecostimp-no-op')
    ```

* 耗时性能排序
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setSortType(TimeCostConfig.CONFIG_SORT_TYPE_START_TIME)
                    .build()
    );
    ```

* Notification开关
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setShowDetailUI(true)
                    .build()
    );
    ```

* 注入jar包开关
    ```
    timeCostConfig {
        jarInject = false
    }
    ```












# TimeCost
根据简单的参数配置，在代码编译阶段自动生成方法耗时统计代码模版，可以将APP运行阶段统计到的耗时方法通过通知消息传达给用户，以此实现用户无感知的方法耗时统计功能。

# 安装
* project/build.gradle 中引入
    ```
    repositories {
        maven {
            url "http://maven.mbd.qiyi.domain/nexus/content/repositories/mbd-android/",
            url "http://maven.mbd.qiyi.domain/nexus/content/repositories/mbd-android-snapshot/"
        }
    }

    buildscript {
        dependencies {
            classpath 'com.iqiyi.tools.build:android-transform-injector:1.0.6'
        }
    }

    allprojects {
        repositories {
            maven {
                url "http://maven.mbd.qiyi.domain/nexus/content/repositories/mbd-android/",
                url "http://maven.mbd.qiyi.domain/nexus/content/repositories/mbd-android-snapshot/"
            }
        }
    }
    ```

* app/build.gradle 中引入
    ```
     apply plugin: 'com.iqiyi.timecost-injector'

     dependencies {
        implementation 'com.iqiyi.video:timecostimp:1.0.0'
        // implementation 'com.iqiyi.video:timecostimp-no-op:1.0.0'
     }

     //timecost
     timeCostConfig {
        //this flag will decide whether the log of the time-cost plugin be printed or not, default value is true
        isDebug = true
        //this is a kit feature of the plugin, set it true to see the time consume of this build
        watchTaskTimeCost = false
        // the plugin will only inject package / class defined in whitePackageList
        whitePackageList = ['org.qiyi.video', 'org.qiyi.android', 'org.qiyi.card', 'com.qiyi.video', 'com.qiyi.android']
        // thp plugin will not inject package, white list has a higher priority than white list
        blackPackageList = []
        // the plugin will inject automatically based on whitePackageList
        autoInject = true
        scope {
            project true //inject app project, default true
            projectLocalDep false //inject app libs(eg: .jar), default false
            subProject true //inject subProject of app project(eg: module), default true
            subProjectLocalDep false //inject subProject libs, default false
            externalLibraries false //inject external libs(eg: .aar), default false
        }
    }
    ```

# 基础使用
Application 中初始化TimeCost
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

# 进阶使用
### gradle插件插桩代码配置
* gradle插件debug开关，控制日志的输出，在代码编译阶段可以详细查看插桩信息。
    ```
    timeCostConfig {
        isDebug = true
    }
    ```
* gradle插件运行耗时统计开关，查看插桩任务的运行时间统计。
    ```
    timeCostConfig {
        watchTaskTimeCost = false
    }
    ```
* 码插桩阶段的白名单/黑名单功能，可以配置包名/类名进行精确匹配。
    ```
    timeCostConfig {
        whitePackageList = ['com.dryseed.timecost']
        blackPackageList = []
    }
    ```
* 无注解自动插桩，会对白名单列表中的文件自动进行插桩，免去大范围注解配置的烦恼。
    ```
    timeCostConfig {
        autoInject = true
    }
    ```
* 字节码注入的代码范围控制，默认是project源码和subproject源码。
    ```
    timeCostConfig {
        scope {
            project true                // inject app project, default true
            projectLocalDep false       // inject app libs(eg: .jar), default false
            subProject true             // inject subProject of app project(eg: module), default true
            subProjectLocalDep false    // inject subProject libs, default false
            externalLibraries false     // inject external libs(eg: .aar), default false
        }
    }
    ```
### 运行时表现配置
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
* Notification耗时提示开关
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setShowDetailUI(true)
                    .build()
    );
    ```
* 耗时时间设置
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setExceedMaxMilliTIme(5000L)   // 最大超时时间
                    .setExceedMilliTime(30L)        // 最小超时时间（时钟时长）
                    .setThreadExceedMilliTime(30L)  // 最小超时时间（Cpu时长）
                    .build()
    );
    ```
* 延时启动设置
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setDelayStartMilliTime(200L)
                    .build()
    );
    ```
* 耗时统计排序设置
    ```
    TimeCostCanary.install(this).config(
            new TimeCostConfig.Builder()
                    .setSortType(TimeCostConstant.CONFIG_SORT_TYPE_START_TIME)
                    .build()
    );
    ```
* 更多功能配置可查看TimeCostConfig.java








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
* 耗时区间配置 Completed
* gradle plugin 插件优化 Completed
* 延时启动统计功能 Completed
* 统计时间设定 Completed
* json导入插桩配置
* 生成可视化报告
* 结合BlockCanary













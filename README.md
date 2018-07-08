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
* 统计时间设定
* json导入插桩配置



## HELP

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












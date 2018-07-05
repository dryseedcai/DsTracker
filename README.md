## TODO LIST
* 耗时所在线程的过滤机制 Completed
* 提供代码插桩阶段的白名单功能 Completed
* 提供无注解自动插桩 Completed
* Release No Operation
* 合入主站APP
* 超时UI界面
* Notification开关
* 统计时间设定
* 耗时性能排序


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

* 提供代码插桩阶段的白名单功能
    ```
    timeCostConfig {
        whitePackageList = ['com.dryseed.timecost']
    }
    ```

* 提供无注解自动插桩
    ```
    timeCostConfig {
        autoInject = true
    }
    ```





















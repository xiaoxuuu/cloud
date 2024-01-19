# cc.xiaoxu.cloud

## 架构

## 结构介绍

```text
基础架构
cloud
├── cloud-service              // [10___] 服务
│   ├── cloud-api-xx           // [10001] xx服务
│   └── cloud-api-xx           // [10002] xx服务
├── cloud-gateway              //         网关
├── cloud-core                 //         插件
│   ├── cloud-core-util        //         基础工具
│   ├── cloud-core-spring      //         Spring 核心模块，此模块依赖于 cloud-core-util
│   ├── cloud-core-mysql       //         mysql 模块，此模块依赖于 cloud-core-spring
│   └── cloud-core-redis       //         redis 模块，此模块依赖于 cloud-core-spring
└── pom.xml                    //         依赖管理
```
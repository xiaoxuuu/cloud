# cc.xiaoxu.cloud

## 架构

## 结构介绍

```text
基础架构
cloud
├── cloud-api              // [10___] 服务
│   ├── cloud-api-demo     // [10001] 测试服务
│   └── cloud-api-file     // [10002] 文件服务
├── cloud-api-single       // [ 9999] 聚合服务
├── cloud-gateway          //         TODO 网关
├── cloud-core             //         插件
│   ├── cloud-core-cache   //         cache 缓存模块   此模块依赖于 cloud-core-spring
│   ├── cloud-core-mysql   //         mysql 数据库模块 此模块依赖于 cloud-core-spring
│   ├── cloud-core-spring  //         Spring 核心模块  此模块依赖于 cloud-core-util
│   └── cloud-core-util    //         基础工具
└── pom.xml                //         依赖管理
```
# 项目简介

此项目旨在提供一个简单上手的 Spring 框架集成脚手架，项目在 `cloud-base-util` 包中内嵌了一些高频使用的工具类。

为了简化开发，可以通过启动 `cloud-api-aggregation` 来聚合调试所有子模块（待测试）。

# 架构介绍

```text
cloud
├── cloud-api              // [10___] 服务
│   ├── cloud-api-demo     // [10001] 测试/演示服务
│   ├── cloud-api-file     // [10002] 文件服务
│   └── cloud-api-my       // [10003] 自有服务
├── cloud-api-aggregation  // [ 9999] 聚合服务，依赖所有 cloud-api 下的服务，通过统一的入口启动，便于测试
├── cloud-base
│   ├── cloud-base-mysql   //         mysql
│   ├── cloud-base-redis   //         redis
│   ├── cloud-base-spring  //         构建一个基础的 Spring Web 服务引用这个即可
│   └── cloud-base-util    //         仅包含基础工具
└── cloud-gateway          // [ 8888] 网关
```

# 功能

## 简单高效的随机工具（开发中）

# 安装

## 打包

```shell
# 打包及推送镜像命令
mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -P tencent -T 12
```

# 部署

打包完成后，服务器拉取对应镜像即可
# cc.xiaoxu.cloud

# 打包发布流程

## 打包

```shell
# 打包及推送命令
mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -P local,releases -T 12
```

## 发布

打包完成后，服务器拉取对应镜像即可

# 架构介绍

```text
基础架构
cloud
├── cloud-api              // [10___] 服务
│   ├── cloud-api-demo     // [10001] 测试/演示服务
│   ├── cloud-api-file     // [10002] 文件服务
│   └── cloud-api-my       // [10003] 自有服务
├── cloud-api-aggregation  // [ 9999] 聚合服务，依赖所有 cloud-api 下的服务，通过统一的入口启动，便于测试
├── cloud-gateway          // [ 8888] 网关
└── cloud-base
    ├── cloud-base-es      //         elasticsearch
    ├── cloud-base-mysql   //         mysql
    ├── cloud-base-redis   //         redis
    ├── cloud-base-spring  //         构建一个基础的 Spring Web 服务引用这个即可
    └── cloud-base-util    //         仅包含基础工具
```
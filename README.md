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
├── cloud-api-aggregation  // [ 9999] 聚合服务
├── cloud-gateway          // [ 8888] 网关
└── cloud-base             //         插件
    ├── cloud-base-redis   //         redis 模块
    ├── cloud-base-core    //         core 核心模块
    ├── cloud-base-mysql   //         mysql 模块
    ├── cloud-base-spring  //         Spring 模块，构建一个基础的 Spring Web 服务引用这个即可
    └── cloud-base-util    //         仅仅包含基础工具
```
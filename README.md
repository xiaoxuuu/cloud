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
│   ├── cloud-api-demo     // [10001] 测试服务
│   ├── cloud-api-file     // [10002] 文件服务
│   └── cloud-api-my       // [10003] 自有服务
├── cloud-api-single       // [ 9999] 聚合服务
├── cloud-gateway          //         TODO 网关
├── cloud-core             //         插件
│   ├── cloud-core-cache   //         cache 缓存模块   此模块依赖于 cloud-core-spring
│   ├── cloud-core-mysql   //         mysql 数据库模块 此模块依赖于 cloud-core-spring
│   ├── cloud-core-spring  //         Spring 核心模块  此模块依赖于 cloud-core-util
│   └── cloud-core-util    //         基础工具
└── pom.xml                //         依赖管理
```
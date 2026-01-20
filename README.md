# TODO

- 常量缓存
- 登录接入
- 微信登录接入
- 微信小程序登录接入
- 分布式未完全接入
- knife4j 接入

# 简介

此项目旨在提供一个简单上手的 Spring 框架集成脚手架，项目在 `cloud-base-util` 包中内嵌了一些高频使用的工具类。

为了简化开发，可以通过启动 `cloud-api-aggregation` 来聚合调试所有子模块（待开发）。

# 架构

```text
cloud
├── cloud-auth              // [10000] 认证服务
├── cloud-api               // [10___] 业务模块
│   ├── cloud-api-ai        // [10004] AI 服务
│   ├── cloud-api-demo      // [10001] 测试/演示服务
│   ├── cloud-api-file      // [10002] 文件服务
│   └── cloud-api-my        // [10003] 自有业务服务
├── cloud-api-all           // [ 9999] 聚合业务服务，依赖所有 cloud-api 下的服务，通过统一的入口启动，便于测试
├── cloud-base              //         基础能力
│   ├── cloud-base-ai       //         ai 能力
│   ├── cloud-base-database //         postgresql
│   ├── cloud-base-doc      //         文档
│   ├── cloud-base-dubbo    //         dubbo
│   ├── cloud-base-redis    //         redis
│   ├── cloud-base-satoken  //         satoken
│   ├── cloud-base-spring   //         构建一个基础的 Spring Web 服务引用这个即可
│   └── cloud-base-util     //         仅包含基础工具
├── cloud-bean              //         实体类模块
│   ├── cloud-bean-ai
│   ├── cloud-bean-common
│   ├── cloud-bean-file
│   └── cloud-bean-my
├── cloud-gateway           // [ 8888] 网关
└── cloud-remote            //         远程调用模块
    └── cloud-remote-file
```

# 功能

## 简单高效的随机工具（开发中）

```java
// 控制随机属性范围
PersonControl control = PersonControl.of()
        .gender(GenderControl.of().gender(Gender.RANDOM))
        .email(EmailControl.of().min(1).max(2))
        .check();
List<Person> some = PersonInitializer.of().control(control).getSome(100);

// 全部属性随机
List<Person> random = PersonInitializer.of().getSome(100);
```

## 简洁美观的异常捕获工具

```java
System.out.println(CatchUtils.of(() -> 1 / 0).handle());
System.out.println(CatchUtils.of(() -> 1 / 0).or(() -> 1 + 1).handle());
System.out.println(CatchUtils.of(() -> 1 / 0).t(9).handle());
System.out.println(CatchUtils.of(() -> 1 / 0).or(() -> 1 + 1).t(9).handle());
System.out.println(CatchUtils.of(() -> 1 + 1).t(99).last(() -> System.err.println("完成")).handle());
```

# 构建

本项目推荐使用 docker 进行部署，故暂未提供传统部署方式。

## 配置

在 pom.xml 文件中配置你的镜像仓库：

```xml
<!-- 镜像仓库信息 -->
<env.namespace>?</env.namespace>
<env.image.warehouse.url>?</env.image.warehouse.url>

<!-- 镜像仓库鉴权信息 -->
<env.image.warehouse.username>?</env.image.warehouse.username>
<env.image.warehouse.password>?</env.image.warehouse.password>
```

## 打包

```shell
# 打包及推送镜像命令
java21 && mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -T 12 -P tencent && mvn clean

mvn -DsendCredentialsOverHttp=true -Dmaven.test.skip=true clean install -T 12 -P ai_local
```

# 发布

打包完成后，服务器拉取对应镜像即可：

> 推荐使用 docker-compose 启动

```shell
docker pull xxx
docker run -itd xxx

# 腾讯云
cd /data/docker/ && sh updateV2.sh -r cloud-api-all && docker logs -f cloud-api-all

# 阿里云
cd /home/root/docker/ && bash update.sh -r api && docker logs -f api
```
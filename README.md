# Tamboot
Tamboot是一个基于 [Spring Boot](https://spring.io/projects/spring-boot)的JAVA服务端开发框架，封装了服务端开发常用的一些基础模块，开发者可基于此快速构建自己的应用。

## 主要模块介绍

* [tamboot-common](#tamboot-common)
* [tamboot-mybatis](#tamboot-mybatis)
* [tamboot-web](#tamboot-web)
* [tamboot-security](#tamboot-security)
* [tamboot-webapp](#tamboot-webapp)
* [tamboot-rocketmq](#tamboot-rocketmq)
* [tamboot-job](#tamboot-job)

### tamboot-common
该模块包含了常用的工具类以及框架的基础接口，其它模块均依赖该模块。

### tamboot-mybatis
该模块基于mybatis，封装了分布式ID生成、分页查询、乐观锁、通用字段统一处理等功能。

该模块有以下两个扩展点：
* InsertStrategy扩展
* UpdateStrategy扩展

##### InsertStrategy扩展
默认的`InsertStrategy`实现是`SnowFlakeIdInsertStrategy`，该策略主要实现了分布式ID的自动插入功能。开发者可自定义策略（比如自动插入创建时间、创建人等信息）来替换默认策略，建议自定义策略继承自`SnowFlakeIdInsertStrategy`。

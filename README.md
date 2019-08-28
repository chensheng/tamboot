# Tamboot
Tamboot是一个基于 [Spring Boot](https://spring.io/projects/spring-boot)的JAVA服务端开发框架，封装了服务端开发常用的一些基础模块，开发者可基于此快速构建自己的应用。

* [模块介绍](#模块介绍)
* [详细教程](https://github.com/chensheng/tamboot/wiki)
* [应用实例](https://github.com/chensheng/tamboot-admin)
* [Demo演示](http://www.tamboot.com)


# 模块介绍

## tamboot-common
该模块包含了常用的工具类以及框架的基础接口，其它模块均依赖该模块。

## tamboot-mybatis
该模块基于mybatis，封装了通用Mapper、分布式ID生成、分页查询、乐观锁、通用字段统一处理等功能。

## tamboot-web
该模块基于spring mvc，封装了统一格式返回值、接口参数校验、系统异常处理、业务异常处理等功能。

## tamboot-security
该模块基于spring security，封装了通用登录、自定义登录、登录凭证存储、访问权限存储、可动态配置的访问权限等功能。

## tamboot-webapp
该模块基于`tamboot-mybatis`、`tamboot-web`、`tamboot-security`的扩展点，实现了统一接口返回格式、基于redis的security信息存储、数据库通用字段自动处理等功能。开发者可基于该模块快速搭建系统，[Tamboot Admin](https://github.com/chensheng/tamboot-admin-back)就是基于该模块搭建的企业应用脚手架项目。

## tamboot-job
该模块基于[quartz定时任务](http://www.quartz-scheduler.org/)，简化了定时任务创建的代码。

## tamboot-xxljob-client
该模块封装了分布式任务调度平台[xxl-job](http://www.xuxueli.com/xxl-job/#/)的客户端，引入该模块，进行简单的配置就能使用。

## tamboot-restdocs-mockmvc
该模块封装了API文档生成工具[Spring Rest Docs](https://spring.io/projects/spring-restdocs)，引入tamboot-restdocs-mockmvc后，只需编写相应的单元测试用例，就能自动生成API文档。

## tamboot-redis
该模块基于[spring-data-redis](https://spring.io/projects/spring-data-redis)，封装了常用的redis操作，并实现了命名空间、分布式锁功能。

## tamboot-rocketmq-client
tamboot-rocketmq-client模块基于[rocketmq-spring](https://github.com/apache/rocketmq-spring)，封装了普通消息、有序消息、延时消息、事务消息的收发方法。

## tamboot-http
tamboot-http模块基于[feign](https://github.com/OpenFeign/feign)和[Apache Http Client](http://hc.apache.org/httpcomponents-client-4.5.x/index.html)，通过注解的方式来定义http接口请求。

## tamboot-wechatty
tamboot-wechatty模块基于[wechatty-project](https://github.com/chensheng/wechatty-project)，实现了springboot的自动配置功能。

## tamboot-excel
tamboot-excel模块基于[POI 4.0.1](https://poi.apache.org/)，封装了基于注解的excel导入及导出方法，并使用[SXSSF](https://poi.apache.org/components/spreadsheet/how-to.html#sxssf)来减少excel大数据量导出的内存消耗。
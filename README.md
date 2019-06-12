# Tamboot
Tamboot是一个基于 [Spring Boot](https://spring.io/projects/spring-boot)的JAVA服务端开发框架，封装了服务端开发常用的一些基础模块，开发者可基于此快速构建自己的应用。

* [模块介绍](#模块介绍)
* [配置信息](#配置信息)

## 模块介绍

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
该模块基于mybatis，封装了分布式ID生成、分页查询、乐观锁、通用字段统一处理等功能。该模块有以下扩展点：
* `InsertStrategy`扩展
* `UpdateStrategy`扩展

###### InsertStrategy扩展
默认的`InsertStrategy`实现是`SnowFlakeIdInsertStrategy`，该策略主要实现了分布式ID的自动插入功能。开发者可自定义策略（比如自动插入创建时间、创建人等信息）来替换默认策略，建议自定义策略继承自`SnowFlakeIdInsertStrategy`。可参考`tamboot-webapp`模块的`CreateInfoInsertStrategy`。

###### UpdateStrategy扩展
默认的`UpdateStrategy`实现是`VersionLockUpdateStrategy`，该策略主要实现了乐观锁的功能。开发者可自定义策略（比如插入修改时间、修改人等信息）来替换默认策略，建议自定义策略继承自`VersionLockUpdateStrategy`。可参考`tamboot-webapp`模块的`ModifyInfoUpdateStrategy`。

### tamboot-web
该模块基于spring mvc，封装了统一格式返回值、接口参数校验、系统异常处理、业务异常处理等功能。该模块有以下扩展点：
* `ResponseBodyDecorator`扩展

###### ResponseBodyDecorator扩展
开发者可实现该接口，根据实际应用场景修改接口返回值。可参考`tamboot-webapp`模块的`PageResponseBodyDecorator`.

### tamboot-security
该模块基于spring security，封装了通用登录、自定义登录、登录凭证存储、访问权限存储、可动态配置的访问权限等功能。该模块有以下扩展点：
* `TokenRepository`扩展
* `RoleBasedPermissionRepository`扩展
* `UserDetailsService`扩展
* `AuthenticationSuccessHandler`扩展
* `AuthenticationFailureHandler`扩展
* `AuthenticationEntryPoint`扩展
* `AccessDeniedHandler`扩展
* `TambootAuthenticationService`扩展

###### TokenRepository扩展
`TokenRepository`的功能是存取用户登录凭证信息，默认的实现是`InMemoryTokenRepository`，将登录凭证信息存储在本地内存。默认存储方式只适用于单机系统，开发者可实现自己的存储方式来适应分布式系统。具体实现可参考`tamboot-webapp`模块的`RedisTokenRepository`，使用redis来存取登录凭证信息。

###### RoleBasedPermissionRepository扩展
`RoleBasedPermissionRepository`的功能是存取访问权限信息，默认的实现是`InMemoryRoleBasedPermissionRepository`，将访问权限信息存储在本地内存。默认存储方式只适用于单机系统，开发者可实现自己的存储方式来适应分布式系统。具体实现可参考`RedisRoleBasedPermissionRepository`，使用redis来存取访问权限信息。

###### UserDetailsService扩展
`UserDetailsService`的功能是根据username查询用户信息（用户名、密码、角色等），默认的实现是`InMemoryUserDetailsManager`，将配置文件中配置的用户信息加载到本地内存，然后再根据username从本地内存查用户。开发者可实现自己的`UserDetailsService`，比如从数据库中查询用户信息，下面是一个例子。
```java
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    @Autowired
    private SystemUserService systemUserService;

    @Autowired
    private SystemRoleService systemRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUserModel user = systemUserService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }

        boolean isUserDisabled = (user.getStatus() == null || user.getStatus().equals(UserStatus.DISABLED));
        String[] roles = systemRoleService.findRolesForUser(user.getId());
        return TambootUserDetails
                .init(user.getId(), user.getUsername(), user.getPassword())
                .disabled(isUserDisabled)
                .roles(roles)
                .build();
    }
}
```

###### AuthenticationSuccessHandler扩展
使用`通用登录`进行登录时，如果登录成功，则会调用`AuthenticationSuccessHandler`，默认实现是`SavedRequestAwareAuthenticationSuccessHandler`，直接重定向到`/`根目录。开发者可以实现自己的handler来进行特定的逻辑处理，比如返回登录成功的json格式数据。具体的实现可参考`tamboot-webapp`模块的`JsonResponseAuthenticationSuccessHandler`。

###### AuthenticationFailureHandler扩展
使用`通用登录`进行登录时，如果登失败，则会调用`AuthenticationFailureHandler`，默认实现是`SimpleUrlAuthenticationFailureHandler`，直接返回401 Unauthorized错误。开发者可以实现自己的handler来进行特定的逻辑处理，比如返回登录失败的json格式数据。具体的实现可参考`tamboot-webapp`模块的`JsonResponseAuthenticationFailureHandler`。

###### AuthenticationEntryPoint扩展
当用户在没有登录的情况下，访问需要登录才能访问的地址时，系统就会调用`AuthenticationEntryPoint`，默认实现方式是重定向到`/`根目录。开发者可以实现自己的`AuthenticationEntryPoint`来进行特定的逻辑处理，比如返回用户未登录的json格式数据。具体的实现可参考`tamboot-webapp`模块的`JsonResponseAuthenticationEntryPoint`。

###### AccessDeniedHandler扩展
当用户访问没有权限访问的地址时，系统就会调用`AccessDeniedHandler`，默认实现是`AccessDeniedHandlerImpl`，直接返回403 Forbidden错误。开发者可以实现自己的`AccessDeniedHandler`来进行特定的逻辑处理，比如返回用户无权限的json格式数据。具体的实现可参考`tamboot-webapp`模块的`JsonResponseAccessDeniedHandler`。

###### TambootAuthenticationService扩展
`TambootAuthenticationService`提供了`login`和`logout`，开发者可使用这两个方法实现自定义的登录、登出接口，比如实现微信公众号的授权登录。下面是一段微信公众号授权登录的伪代码。
```java
@Service
@Transactional(readOnly = true)
public class WxmpServiceImpl implements WxmpService {
	@Autowired
	private MpAppContext mpAppContext;
	
	@Autowired
	private TambootAuthenticationService authenticationService;
	
	@Autowired
	private SystemUserService userService;	

	@Override
	@Transactional(readOnly = false)
	public String login(HttpServletRequest request, HttpServletResponse response, String code) {
		if (StringUtil.isEmpty(code)) {
			throw new BusinessException("请求参数code不能为空");
		}
		
		AuthAccessTokenResponse authResp = mpAppContext.getAuthHelper().fetchAuthAccessToken(code);
		if (authResp == null || !authResp.isOk()) {
			throw new BusinessException("微信授权失败");
		}
		
		SystemUser existingUser = userService.findWxmpUser(authResp.getOpenId());
		if (existingUser != null) {
			return authenticationService.login(existingUser.getUsername(), request, response);
		}
		
		SystemUser newUser = userService.createWxmpUser(authResp.getOpenId);
		return authenticationService.login(newUser.getUsername(), request, response);
	}
}
```

### tamboot-webapp
该模块基于`tamboot-mybatis`、`tamboot-web`、`tamboot-security`的扩展点，实现了统一接口返回格式、基于redis的security信息存储、数据库通用字段自动处理等功能。开发者可基于该模块快速搭建系统，[Tamboot Admin](https://github.com/chensheng/tamboot-admin-back)就是基于该模块搭建的企业应用脚手架项目。

### tamboot-rocketmq
该模块封装了[rocketmq](http://rocketmq.apache.org/)客户端API，简化了mq消息收发的代码。

###### 发送消息
```java
@Service
public class RocketmqTestServiceImpl implements RocketmqTestService {
    @Autowired
    private SimpleMQProducer simpleProducer;

    public SendResult send(String content) {
        Message msg = new Message("testTopic", "testTag", content.getBytes(RemotingHelper.DEFAULT_CHARSET));
        return simpleProducer.send(msg);
    }
}
```

###### 接收消息
```java
@RocketMQConsumer(consumerGroup = "testGroup", topic = "testTopic")
public class TestTopicListener implements MessageListenerConcurrently {
	private AtomicInteger consumeTimes = new AtomicInteger(0);

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		if (consumeTimes.incrementAndGet() % 2 == 0) {
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		
		for (MessageExt msg : msgs) {
			try {
				String body = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
			} catch (UnsupportedEncodingException e) {
				logger.error(ExceptionUtils.getStackTraceAsString(e));
			}
		}
		
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}
```
更多例子可参考`tamboot-sample`模块。

### tamboot-job
该模块基于[quartz定时任务](http://www.quartz-scheduler.org/)，简化了定时任务创建的代码。

###### 创建定时任务类
```java
package com.tamboot.sample.job;

import com.tamboot.job.core.Job;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleJob implements Job {
    private Log logger = LogFactory.getLog(getClass());

    @Override
    public void execute(Map<String, Object> params) {
        logger.info("sample job is executing");
        if (params == null) {
            logger.info("no job params found");
        } else {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                logger.info("job param: name["+entry.getKey()+"] value["+entry.getValue()+"]");
            }
        }
    }
}
```

###### 在配置文件中添加定时任务
```yml
tamboot:
  job:
    refreshCron: 0 0/1 * * * ?
    threadCount: 5
    jobs:
      - jobId: 1
        jobBeanName: sampleJob
        triggerCron: 0 0/1 * * * ?
        params:
          param1: value1
          param2: value2
```

该模块有以下扩展点:
* `JobDataRepository`扩展

###### JobDataRepository扩展
`JobDataRepository`的功能是存储任务数据，默认的实现是`InMemoryJobDataRepository`，从配置文件中加载任务数据。开发者可以实现自己的`JobDataRepository`，比如从数据库中加载任务数据，具体可参考`tamboot-sample`模块的`DatabaseJobDataRepository`。

## 配置信息

* [tamboot-mybatis配置](#tamboot-mybatis配置)
* [tamboot-web配置](#tamboot-web配置)
* [tamboot-security配置](#tamboot-security配置)
* [tamboot-rocketmq配置](#tamboot-rocketmq配置)
* [tamboot-job配置](#tamboot-job配置)

### tamboot-mybatis配置
参数|说明|类型|默认值
-----|-----|-----|-----
mybatis.ignoreInterceptor|是否使用redis来保存登录凭证、访问权限等系统安全有关的数据。默认为false，表示将这些数据保存在本地内存中。|Boolean|false
mybatis.throwVersionLockException|当发生乐观锁错误时，是否抛出异常。默认为false，表示不抛出异常，些时需要根据update语句的返回值判断数据是否更新成功。建议将该项置设为true。|Boolean|false
mybatis.snowFlake.*|分布式id生成算法的配置||
mybatis.snowFlake.dataCenterId|数据中心id，从1到1024。当应用要分布式部署时，不同服务器的应用配置不同的值。|Long|
mybatis.snowFlake.generatorStartTime|id生成器的开始时间的毫秒数，不能大于当前时间，一般采用默认值即可。|Long|1493737860828
mybatis.configuration.mapUnderscoreToCamelCase|自动将数据库表中带下划线的字段与Model中的驼峰命名的字段对应起来，使用本框架需设为true。|Boolean|false
mybatis.*|更多的配置可参考[MybatisProperties](https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/MybatisProperties.java)和[mybatis设置](http://www.mybatis.org/mybatis-3/zh/configuration.html#settings)||

### tamboot-web配置
参考[spring.mvc.*配置](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/#common-application-properties)和[WebMvcProperties](https://github.com/spring-projects/spring-boot/blob/v2.1.5.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties.java)。

### tamboot-security配置
参数|说明|类型|默认值
-----|-----|-----|-----
spring.security.useRedisRepo|是否使用redis来保存登录凭证、访问权限等系统安全有关的数据。默认为false，表示将这些数据保存在本地内存中。|Boolean|false
spring.security.loginPath|登录接口地址。如果该项未配置，则系统内置的登录接口不可用，开发者可实现自己的登录接口。|String|
spring.security.ignoringAntMatchers|绕过权限检查的接口请求地址，采用ant path格式。比如一些接口数据不需要用户登录就能访问，则可通过该配置项绕过权限检查。|String[]|
spring.security.interceptAntMatcher|检查权限时，只检查满足指定ant path格式的接口请求地址，其它地址均绕过。默认为空，表示检查除了ignoringAntMatchers外的所有接口地址。|String|
spring.security.tokenExpirySeconds|登录凭证失效时长，单位:秒，默认为一个月。|Integer|2592000
spring.security.rejectPublicInvocations|当系统未配置访问权限信息时，是否拒绝所有的接口访问请求。|Boolean|true

### tamboot-rocketmq配置
参数|说明|类型|默认值
-----|-----|-----|-----
tamboot.rocketmq.namesrv|rocketmq的name server地址，比如:127.0.0.1:9876|String|
tamboot.rocketmq.simpleProducer.*|普通消息发送配置||
tamboot.rocketmq.simpleProducer.group|消息所属组|String|
tamboot.rocketmq.simpleProducer.sendMsgTimeout|发送消息超时时间(单位：毫秒)。|Integer|3000
tamboot.rocketmq.simpleProducer.compressMsgBodyOverHowMuch|消息体超过多少字节时进行压缩(单位：byte)。|Integer|4096（即4K）
tamboot.rocketmq.simpleProducer.retryTimesWhenSendFailed|同步发送消息失败时的重试次数。|Integer|2
tamboot.rocketmq.simpleProducer.retryTimesWhenSendAsyncFailed|异步发送消息失败时的重试次数。|Integer|2
tamboot.rocketmq.simpleProducer.maxMessageSize|消息体所占最大字节数(单位：byte)。|Integer|4194304（即4M）
tamboot.rocketmq.simpleProducer.retryAnotherBrokerWhenNotStoreOk|当一个broker不可用时，尝试使用另一个broker。|Boolean|false
tamboot.rocketmq.transactionProducer.*|事务消息发送配置，具体配置项与simpleProducer一样。||

### tamboot-job配置
参数|说明|类型|默认值
-----|-----|-----|-----
tamboot.job.refreshCron|刷新任务数据的时机，默认是每一分钟刷新一次，使用cron表达式。|String|0 0/1 * * * ?
tamboot.job.threadCount|运行任务的线程池的总线程数|Integer|5
tamboot.job.jobs|定时任务数据(使用默认的JobDataRepository时有效)|JobData[]|
tamboot.job.jobs[].jobId|定时任务id，不能重复。|String|
tamboot.job.jobs[].jobBeanName|定时任务bean的名称，bean必须实现自com.tamboot.job.core.Job。|String|
tamboot.job.jobs[].triggerCron|定时任务触发的时机，使用cron表达式。|String|
tamboot.job.jobs[].params|定时任务参数|Map<String, Object>|

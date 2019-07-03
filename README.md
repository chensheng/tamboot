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
* [tamboot-xxljob-client](#tamboot-xxljob-client)
* [tamboot-restdocs-mockmvc](#tamboot-restdocs-mockmvc)
* [tamboot-redis](#tamboot-redis)

### tamboot-common
该模块包含了常用的工具类以及框架的基础接口，其它模块均依赖该模块。

### tamboot-mybatis
该模块基于mybatis，封装了通用Mapper、分布式ID生成、分页查询、乐观锁、通用字段统一处理等功能。下面将具体介绍以下功能：
* `通用Mapper`
* `InsertStrategy扩展`
* `UpdateStrategy扩展`

###### 通用Mapper
通用Mapper实现了常用的增删改查方法，只需要继承自CommonMapper就能直接使用这些方法。
```java
public interface SystemUserMapper extends CommonMapper<SystemUserModel, Long> {
}
```

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

### tamboot-xxljob-client
该模块封装了分布式任务调度平台[xxl-job](http://www.xuxueli.com/xxl-job/#/)的客户端，引入该模块，进行简单的配置就能使用。

`简单配置`
```yaml
tamboot:
  xxljob:
    client:
      appName: tamboot-sample
      adminAddresses: http://127.0.0.1:8080/xxl-job-admin
```

`实现定时任务`
```java
@Component
@JobHandler("testXxlJob")
public class TestXxlJob extends IJobHandler {
    private static final Logger logger = LoggerFactory.getLogger(TestXxlJob.class);

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        logger.info("test xxl job is running");
        return ReturnT.SUCCESS;
    }
}
```

### tamboot-restdocs-mockmvc
该模块封装了API文档生成工具[Spring Rest Docs](https://spring.io/projects/spring-restdocs)，引入tamboot-restdocs-mockmvc后，只需编写相应的单元测试用例，就能自动生成API文档。

`添加maven插件`
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.asciidoctor</groupId>
            <artifactId>asciidoctor-maven-plugin</artifactId>
            <version>1.5.3</version>
            <executions>
                <execution>
                    <id>generate-docs</id>
                    <phase>prepare-package</phase>
                    <goals>
                        <goal>process-asciidoc</goal>
                    </goals>
                    <configuration>
                        <backend>html</backend>
                        <doctype>book</doctype>
                        <outputDirectory>${basedir}/src/main/apidoc</outputDirectory>
                    </configuration>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.restdocs</groupId>
                    <artifactId>spring-restdocs-asciidoctor</artifactId>
                    <version>2.0.3.RELEASE</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

`添加首页和数据字典文档`

为了避免接口文档过于分散，开发者需单独添加一个测试类，生成数据字典的文档，并将所有子文档整合在一起。
```java
@AsciidocConfig(ignore = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZIndexDocTest extends TambootDocTest {

    @Test
    public void zIndex() {
        AsciidocGenerator.createIndexDoc(this.context, getClass().getPackage().getName(), this.asciidocPath);
    }

    @Test
    public void dictionary() {
        AsciidocGenerator.createDictionaryDoc(this.asciidocPath,
                dictionaryItem(ResponseType.class, "接口返回码"),
                dictionaryItem(UserStatus.class, "用户状态")
        );
    }
}
```

`编写测试用例`

开发者只需要继承TambootDocTest，并针对相应的接口编写单元测试用例。
```java
@AsciidocConfig(title = "个人信息", orderIndex = 2)
@WithUserDetails
public class CommonUserDocTest extends TambootDocTest {

    @Test
    @AsciidocConfig(title = "获取个人信息", orderIndex = 1)
    public void details() throws Exception {
        this.mockMvc
                .perform(getJson( "/common/user/details"))
                .andExpect(status().isOk())
                .andDo(document(
                        requestParameters(),
                        commonResponseFields(
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("数据"),
                                fieldWithPath("data.userId").type(JsonFieldType.STRING).description("用户id"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("用户名"),
                                fieldWithPath("data.password").type(JsonFieldType.STRING).description("密码"),
                                fieldWithPath("data.accountNonExpired").type(JsonFieldType.BOOLEAN).description("账号未过期"),
                                fieldWithPath("data.accountNonLocked").type(JsonFieldType.BOOLEAN).description("账号未被锁"),
                                fieldWithPath("data.credentialsNonExpired").type(JsonFieldType.BOOLEAN).description("密码未过期"),
                                fieldWithPath("data.enabled").type(JsonFieldType.BOOLEAN).description("用户是否启用"),
                                fieldWithPath("data.roles[]").type(JsonFieldType.ARRAY).description("拥有的角色编码"),
                                fieldWithPath("data.authorities[]").type(JsonFieldType.ARRAY).description("权限信息"),
                                fieldWithPath("data.authorities[].authority").type(JsonFieldType.STRING).description("权限值").optional()
                        ))
                );
    }

    @Test
    @AsciidocConfig(title = "修改密码", orderIndex = 2, snippets = AsciidocConfig.BODY_PARAMS_SNIPPETS)
    public void updatePassword() throws Exception {
        UpdatePasswordForm form = new UpdatePasswordForm();
        form.setOldPassword("Qwb123@456");
        form.setNewPassword("Wbm@123456q");
        this.mockMvc
                .perform(postJson("/common/user/updatePassword", form))
                .andExpect(status().isOk())
                .andDo(document(
                        requestBodyFields(
                                fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("* 原密码"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("* 新密码，密码必须由数字、字母、特殊字符(_#@!)组成，且不能少于8位。")
                        ),
                        commonResponseFields(
                                fieldWithPath("data").ignored().optional()
                        )
                ));
    }
}
```

`生成文档`

只需执行mvn package命令，就会在src/main/apidoc目录下生成API文档。

`模拟登录`

当测试一些需要登录后才能操作的API时，开发者只需要在测试类或测试方法上添加@WithUserDetails(value = "user")注解（value值为对应的用户名，且系统中必须存在该用户）。

`@AsciidocConfig注解`

参数|说明|类型|默认值
-----|-----|-----|-----
title|文档或接口的标题。如果不设置该项，则默认使用测试类或测试方法的名称。|String|
orderIndex|文档的排序，值越小排序越靠前。|Integer|0
snippets|在文档中使用哪些代码片段，系统中内置了QUERY_PARAMS_SNIPPETS（请求参数都在url参数上）、BODY_PARAMS_SNIPPETS（请求参数都在body中）、PATH_PARAMS_SNIPPETS（请求参数都在url路径上）三种类型。如果系统内置的代码片段不满足要求，开发者可以填写自定义的代码片段。|String|QUERY_PARAMS_SNIPPET
id|生成的adoc文件的id。测试类id生成方式为：将类名转成中划线分隔的字符串，并去除DocTest后缀。测试方法id生成方式为：类id-方法名|String|
ignore|是否跳过文档的生成|Boolean|false

### tamboot-redis
该模块基于[spring-data-redis](https://spring.io/projects/spring-data-redis)，封装了常用的redis操作，并实现了命名空间、分布式锁功能。

`创建命名空间`

命名空间可以防止redis的key产生冲突，一般使用枚举值enum。

```java
public enum CustomRedisNamespace {
    TOKEN("token", "用户登录凭证"),
    CONFIG("config", "系统安全配置信息");

    private String code;

    private String msg;

    CustomRedisNamespace(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
```

`创建redis模板`

开发者需继承`TambootRedisTemplate`，并将其注册为spring的bean。

```java
@Component
public class CustomRedisTemplate extends TambootRedisTemplate<CustomRedisNamespace> {
    @Autowired
    private RedisTemplate redisTemplate;

    public SecurityRedisTemplate() {
        super(redisTemplate);
    }

    @Override
    protected String resolveNamespaceValue(CustomRedisNamespace namespace) {
        return "customApp:" + namespace.getCode();
    }
}
```

`使用redis模板`

```java
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private CustomRedisTemplate customRedisTemplate;

    @Override
    public void test() {
        customRedisTemplate.set(CustomRedisNamespace.CONFIG, "key", "value");
    }
}
```

`分布式锁方法`

方法 | 说明
-----|-----
lock(T namespace, String key, Duration timeout) | 尝试获取锁。如果该锁还未被释放，则获取失败，返回false。反之则获取成功，返回true，且经过timeout时长后，该锁将自动释放。
releaseLock(T namespace, String key) | 手动释放锁。在某些场景下，锁可能已获取成功，但后续的业务处理出现异常，需要释放锁来避免资源的占用，此时可以在finally中使用该方法来手机释放锁。
lockInDuration(T namespace, String key, Duration duration, long concurrent) | 尝试获取锁。该锁表示某个时间段内最多允许n个线程或进程同时获得锁。


## 配置信息

* [tamboot-mybatis配置](#tamboot-mybatis配置)
* [tamboot-web配置](#tamboot-web配置)
* [tamboot-security配置](#tamboot-security配置)
* [tamboot-rocketmq配置](#tamboot-rocketmq配置)
* [tamboot-job配置](#tamboot-job配置)
* [tamboot-xxljob-client配置](#tamboot-xxljob-client配置)

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
参考Spring Boot的配置[spring.mvc.*](https://docs.spring.io/spring-boot/docs/2.1.5.RELEASE/reference/htmlsingle/#common-application-properties)和[WebMvcProperties](https://github.com/spring-projects/spring-boot/blob/v2.1.5.RELEASE/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties.java)。

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

### tamboot-xxljob-client配置
参数|说明|类型|默认值
-----|-----|-----|-----
tamboot.xxljob.client.appName|执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册|String|
tamboot.xxljob.client.adminAddresses|调度中心部署跟地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；|String|
tamboot.xxljob.client.ip|执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯实用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；|String|
tamboot.xxljob.client.port|执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；|Integer|9999
tamboot.xxljob.client.accessToken|执行器通讯TOKEN [选填]：非空时启用；|String|
tamboot.xxljob.client.logPath|执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；|String|
tamboot.xxljob.client.logRetentionDays|执行器日志保存天数 [选填] ：值大于3时生效，启用执行器Log文件定期清理功能，否则不生效；|Integer|
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
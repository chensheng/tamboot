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

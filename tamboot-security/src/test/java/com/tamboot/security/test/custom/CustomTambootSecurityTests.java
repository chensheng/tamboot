package com.tamboot.security.test.custom;

import com.tamboot.security.core.PasswordEncoderFactory;
import com.tamboot.security.token.TokenRepositoryFactory;
import com.tamboot.security.util.SafeSecurityContextHolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CustomConfig.class})
@WebAppConfiguration
public class CustomTambootSecurityTests {
    @Autowired(required = false)
    private PasswordEncoderFactory passwordEncoderFactory;

    @Autowired(required = false)
    private TokenRepositoryFactory tokenRepositoryFactory;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void testPasswordEncoder() {
        Assert.assertNotNull(passwordEncoderFactory);
        Assert.assertTrue(passwordEncoderFactory.get().getClass().equals(CustomPasswordEncoder.class));
    }

    @Test
    public void testTokenRepository() {
        Assert.assertNotNull(tokenRepositoryFactory);
        Assert.assertTrue(tokenRepositoryFactory.get().getClass().equals(CustomTokenRepository.class));
    }

    @Test
    public void testUserDetailsWithoutUser() {
        UserDetails userDetails = SafeSecurityContextHolder.getUserDetails();
        Assert.assertNull(userDetails);
    }

    @Test
    @WithMockUser
    public void testUserDetailsWithUser() {
        UserDetails userDetails = SafeSecurityContextHolder.getUserDetails();
        Assert.assertNotNull(userDetails);
    }

    @Test
    public void testLoginWithInvalidUser() throws Exception {
        mvc.perform(post("/login")
                .param("username", "invalidUser")
                .param("password", "123456"))
        .andExpect(status().isOk())
        .andExpect(content().string("login fail"));
    }

    @Test
    public void testLoginWithInvalidPassword() throws Exception {
        mvc.perform(post("/login")
                .param("username", "tamboot")
                .param("password", "abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("login fail"));
    }

    @Test
    public void testLoginWithUser() throws Exception {
        mvc.perform(post("/login")
                .param("username", "tamboot")
                .param("password", "123456"))
        .andExpect(status().isOk())
        .andExpect(content().string("login success tamboot"));
    }

    @Test
    public void testNoAuthentication() throws Exception {
        mvc.perform(get("/user/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("please login first"));
    }

    @Test
    @WithMockUser
    public void testUserResourcesWithUserRole() throws Exception {
        mvc.perform(get("/user/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("tamboot"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void testUserResourcesWithManagerRole() throws Exception {
        mvc.perform(get("/user/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("tamboot"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void testManagerResourcesWithManagerRole() throws Exception {
        mvc.perform(get("/manager/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("manager"));
    }

    @Test
    @WithMockUser
    public void testAccessDenied() throws Exception {
        mvc.perform(get("/manager/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("access denied"));
    }

    @Test
    public void testAuthenticationService() throws Exception {
        String token = mvc.perform(post("/anonymous/manuallyLogin")
                .param("username", "tamboot"))
                .andReturn().getResponse().getCookie("token").getValue();
        Assert.assertNotNull(token);
        Assert.assertNotNull(tokenRepositoryFactory);
        SecurityContext securityContext = tokenRepositoryFactory.get().load(token, -1);
        Assert.assertNotNull(securityContext);
        Assert.assertNotNull(securityContext.getAuthentication());
        Assert.assertNotNull(securityContext.getAuthentication().getPrincipal());
        Assert.assertTrue(securityContext.getAuthentication().getPrincipal() instanceof UserDetails);
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        Assert.assertEquals("tamboot", userDetails.getUsername());

        mvc.perform(post("/anonymous/manuallyLogout")
                .cookie(new Cookie("token", token)))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
        Assert.assertTrue(!tokenRepositoryFactory.get().contains(token, -1));
    }
}

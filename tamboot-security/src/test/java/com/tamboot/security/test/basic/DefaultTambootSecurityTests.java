package com.tamboot.security.test.basic;

import com.tamboot.security.config.TambootSecurityProperties;
import com.tamboot.security.core.PasswordEncoderFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DefaultTambootSecurityTests {
    @Autowired(required = false)
    private PasswordEncoderFactory passwordEncoderFactory;

    @Autowired(required = false)
    private TambootSecurityProperties properties;

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
    public void testProperties() {
        Assert.assertNotNull(properties);
        List<TambootSecurityProperties.RoleBasedPermission> permissions = properties.getRoleBasedPermissions();
        Assert.assertNotNull(permissions);
        Assert.assertTrue(permissions.size() == 3);
        Assert.assertEquals("/**", permissions.get(0).getUrlAntPattern());
        Assert.assertEquals("ADMIN,USER", permissions.get(0).getRoles());
        Assert.assertEquals("/user/**", permissions.get(1).getUrlAntPattern());
        Assert.assertEquals("ADMIN,USER", permissions.get(1).getRoles());
        Assert.assertEquals("/admin/**", permissions.get(2).getUrlAntPattern());
        Assert.assertEquals("ADMIN", permissions.get(2).getRoles());
    }

    @Test
    public void testPasswordEncoderFactory() {
        Assert.assertNotNull(passwordEncoderFactory);
        String rawPassword = "TAMBOOT123456";
        String encodedPwd = passwordEncoderFactory.get().encode(rawPassword);
        Assert.assertNotNull(encodedPwd);
        Assert.assertTrue(passwordEncoderFactory.get().matches(rawPassword, encodedPwd));
        Assert.assertTrue(!passwordEncoderFactory.get().matches("123456", encodedPwd));
    }

    @Test
    public void testLoginFailure() throws Exception {
        mvc.perform(post("/login")
                .param("username", "user")
                .param("password", "123456")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        mvc.perform(post("/login")
                .param("username", "user")
                .param("password", "tamboot123456")
        ).andExpect(status().is3xxRedirection());
    }

    @Test
    public void testWithNoAuthentication() throws Exception {
        mvc.perform(get("/user/name")).andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void testAccessDenied() throws Exception{
        mvc.perform(get("/admin/name")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    public void testAccessPermitted() throws Exception {
        mvc.perform(get("/admin/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    @WithMockUser
    public void testWithUserRole() throws Exception {
        mvc.perform(get("/user/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("tamboot"));
    }

    @Test
    @WithMockUser(value = "admin", roles = {"ADMIN"})
    public void testWithAdminRole() throws Exception {
        mvc.perform(get("/user/name"))
                .andExpect(status().isOk())
                .andExpect(content().string("tamboot"));
    }

    @Test
    public void testAnonymous() throws Exception {
        mvc.perform(get("/anonymous/data"))
                .andExpect(status().isOk())
                .andExpect(content().string("data"));
    }

}

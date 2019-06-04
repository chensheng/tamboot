package com.tamboot.web.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {TestController.class})
public class TambootWebTests {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testGet() throws Exception {
        this.mvc.perform(get("/test/get").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"username\":\"Tam Boot\",\"age\":1}}"));
    }

    @Test
    public void testGetXml() throws Exception {
        this.mvc.perform(get("/test/get").accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(content().xml("<TambootResponse><code>1</code><msg>success</msg><data><username>Tam Boot</username><age>1</age></data></TambootResponse>"));
    }

    @Test
    public void testGetNull() throws Exception {
        this.mvc.perform(get("/test/getNull").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"1\",\"msg\":\"success\"}"));
    }

    @Test
    public void testBusinessException() throws Exception {
        this.mvc.perform(get("/test/businessException").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"0\",\"msg\":\"business exception\"}"));
    }

    @Test
    public void testSystemException() throws Exception {
        this.mvc.perform(get("/test/systemException").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"9999\",\"msg\":\"exception\"}"));
    }

    @Test
    public void testBindException() throws Exception {
        this.mvc.perform(get("/test/queryByUsername").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"0\",\"msg\":\"username must not be null\"}"));
    }

    @Test
    public void testQueryByUsername() throws Exception {
        this.mvc.perform(get("/test/queryByUsername").param("username", "test").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"username\":\"test\",\"age\":1}}"));
    }

    @Test
    public void testDateFormat() throws Exception {
        this.mvc.perform(
                get("/test/dateFormat")
                        .param("date", "2019-03-28")
                        .param("datetime", "2019-03-28 23:23:23")
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"code\":\"1\",\"msg\":\"success\",\"data\":{\"date\":\"2019-03-28\",\"datetime\":\"2019-03-28 23:23:23\"}}"));
    }

    @Test
    public void testGetTextPlain() throws Exception {
        this.mvc.perform(get("/test/getTextPlain")).andExpect(content().string("Tam Boot"));
    }
}

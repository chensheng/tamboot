package com.tamboot.restdocs.mockmvc;

import com.tamboot.common.tools.mapper.JsonMapper;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.*;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class TambootDocTest {
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mockMvc;

    protected String asciidocPath = "src/main/asciidoc";

    public TambootDocTest() {

    }

    public TambootDocTest(String asciidocPath) {
        this.asciidocPath = asciidocPath;
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    protected PathParametersSnippet pathParameters(ParameterDescriptor... parameterDescriptors) {
        return RequestDocumentation.relaxedPathParameters(parameterDescriptors);
    }

    protected RequestParametersSnippet requestParameters(ParameterDescriptor... parameterDescriptors) {
        return RequestDocumentation.relaxedRequestParameters(parameterDescriptors);
    }

    protected RequestFieldsSnippet requestBodyFields(FieldDescriptor... fieldDescriptors) {
        return PayloadDocumentation.relaxedRequestFields(fieldDescriptors);
    }

    protected ResponseFieldsSnippet commonResponseFields(FieldDescriptor ...fieldDescriptors) {
        List<FieldDescriptor> fieldDescriptorList = new ArrayList<FieldDescriptor>();
        fieldDescriptorList.add(fieldWithPath("code").type(JsonFieldType.STRING).description("状态码"));
        fieldDescriptorList.add(fieldWithPath("msg").type(JsonFieldType.STRING).description("提示消息"));
        fieldDescriptorList.add(fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("是否成功").optional());
        if (fieldDescriptors != null) {
            fieldDescriptorList.addAll(Arrays.asList(fieldDescriptors));
        }
        return responseFields(fieldDescriptorList);
    }

    protected RestDocumentationResultHandler document(Snippet... snippets) {
        String docItemId = getCurrentDocItemId();
        return MockMvcRestDocumentation.document(docItemId, snippets);
    }

    protected String getCurrentDocItemId() {
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
        Class<?> currentClass = getClass();
        String currentClassName = currentClass.getName();
        for (StackTraceElement ele : eles) {
            if (currentClassName.equals(ele.getClassName())) {
                return AsciidocGenerator.parseChildDocItemId(currentClass, ele.getMethodName());
            }
        }
        return null;
    }

    protected MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables) {
        return RestDocumentationRequestBuilders.get(urlTemplate, urlVariables);
    }

    protected MockHttpServletRequestBuilder post(String urlTemplate, Object... urlVariables) {
        return RestDocumentationRequestBuilders.post(urlTemplate, urlVariables);
    }

    protected StatusResultMatchers status() {
        return MockMvcResultMatchers.status();
    }

    protected ParameterDescriptor parameterWithName(String name) {
        return RequestDocumentation.parameterWithName(name);
    }

    protected FieldDescriptor fieldWithPath(String path) {
        return PayloadDocumentation.fieldWithPath(path);
    }

    protected MockHttpServletRequestBuilder getJson(String urlTemplate, Object... urlVariables) {
        return get(urlTemplate, urlVariables).accept(MediaType.APPLICATION_JSON_UTF8);
    }

    protected MockHttpServletRequestBuilder postJson(String urlTemplate, Object body, Object... urlVariables) {
        MockHttpServletRequestBuilder builder = post(urlTemplate, urlVariables)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);
        if (body != null) {
            builder.content(JsonMapper.nonNullMapper().toJson(body));
        }
        return builder;
    }

    protected AsciidocGenerator.DictionaryItem dictionaryItem(Class<? extends Enum> dictionaryType, String title) {
        return new AsciidocGenerator.DictionaryItem(dictionaryType, title);
    }

    protected AsciidocGenerator.DictionaryItem dictionaryItem(Class<? extends Enum> dictionaryType, String title, String codeFieldName, String msgFieldName) {
        return new AsciidocGenerator.DictionaryItem(dictionaryType, title, codeFieldName, msgFieldName);
    }

    @Test
    @AsciidocConfig(ignore = true)
    public void zLastTest() {
        AsciidocGenerator.createChildDoc(getClass(), asciidocPath);
    }
}
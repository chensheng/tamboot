package com.tamboot.restdocs.mockmvc;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.common.tools.collection.CollectionUtil;
import com.tamboot.common.tools.io.FileUtil;
import com.tamboot.common.tools.reflect.ReflectionUtil;
import com.tamboot.common.tools.text.TextUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class AsciidocGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AsciidocGenerator.class);

    private static final String INDEX_DOC_ID = "index";

    private static final String DICTIONARY_DOC_ID = "dictionary";

    public static boolean createIndexDoc(WebApplicationContext context, String docPackage, String outputDirectory) {
        if (context == null || TextUtil.isEmpty(docPackage) || TextUtil.isEmpty(outputDirectory)) {
            return false;
        }

        String indexDocId = INDEX_DOC_ID;
        String indexDocPath = outputDirectory.concat(File.separator).concat(indexDocId).concat(".adoc");
        String indexDocText = generateIndexDocText(context, docPackage, outputDirectory);

        return createDocFile(indexDocPath, indexDocText);
    }

    public static boolean createDictionaryDoc(String outputDirectory, DictionaryItem... dictionaries) {
        if (TextUtil.isEmpty(outputDirectory)) {
            return false;
        }

        String dictDocId = DICTIONARY_DOC_ID;
        String dictDocPath = outputDirectory.concat(File.separator).concat(dictDocId).concat(".adoc");
        String dictDocText = generateDictionaryDocText(dictionaries);

        return createDocFile(dictDocPath, dictDocText);
    }

    public static boolean createChildDoc(Class<?> testType, String outputDirectory) {
        if (testType == null || TextUtil.isEmpty(outputDirectory)) {
            return false;
        }

        if (ignoreChildDoc(testType)) {
            return false;
        }

        String childDocId = resolveChildDocId(testType);
        String childDocPath = outputDirectory.concat(File.separator).concat(childDocId).concat(".adoc");
        String childDocTitle = resolveChildDocTitle(testType);
        List<DocItem> childDocItems = resolveChildDocItems(testType, childDocId);
        String docText = generateChildDocText(childDocTitle, childDocItems);

        return createDocFile(childDocPath, docText);
    }

    public static String parseChildDocItemId(Class<?> testType, String methodName) {
        if (testType == null || TextUtil.isEmpty(methodName)) {
            return null;
        }

        String childDocId = resolveChildDocId(testType);
        Method method = ReflectionUtil.getMethod(testType, methodName);
        return resolveChildDocItemId(method, childDocId);
    }

    private static boolean createDocFile(String docPath, String docText) {
        try {
            File file = new File(docPath);
            if (FileUtil.isFileExists(file)) {
                FileUtil.deleteFile(file);
            }

            FileUtil.write(docText, file);
            return true;
        } catch (IOException e) {
            logger.error(ExceptionUtil.stackTraceText(e));
        }
        return false;
    }

    private static String generateIndexDocText(WebApplicationContext context, String docPackage, String outputDirectory) {
        StringBuilder docText = new StringBuilder();
        docText.append("= API 文档").append("\n")
                .append(":doctype: book").append("\n")
                .append(":icons: font").append("\n")
                .append(":source-highlighter: highlightjs").append("\n")
                .append(":toc: left").append("\n")
                .append(":toc-title: 目录").append("\n")
                .append(":toclevels: 2").append("\n")
                .append(":sectlinks:").append("\n")
                .append(":operation-curl-request-title: 请求示例").append("\n")
                .append(":operation-http-response-title: 返回示例").append("\n")
                .append(":operation-request-fields-title: 请求参数").append("\n")
                .append(":operation-request-parameters-title: 请求参数").append("\n")
                .append(":operation-path-parameters-title: 路径参数").append("\n")
                .append(":operation-response-fields-title: 返回域").append("\n");

        String dictDocFilePath = outputDirectory.concat(File.separator).concat(DICTIONARY_DOC_ID).concat(".adoc");
        if (FileUtil.isFileExists(dictDocFilePath)) {
            docText.append("\n").append("include::").append(DICTIONARY_DOC_ID).append(".adoc[]").append("\n");
        }

        Set<Class<?>> testClasses = ClassScanner.scan(context, docPackage);
        if (CollectionUtil.isEmpty(testClasses)) {
            return docText.toString();
        }

        List<Class<?>> docTestClasses = new ArrayList<Class<?>>();
        for (Class<?> testClass : testClasses) {
            if (isChildDocClass(testClass)) {
                docTestClasses.add(testClass);
            }
        }

        docTestClasses.sort(docClassComparator);
        for (Class<?> docTestClass : docTestClasses) {
            String childDocId = resolveChildDocId(docTestClass);
            docText.append("\n").append("include::").append(childDocId).append(".adoc[]").append("\n");
        }
        return docText.toString();
    }

    private static String generateDictionaryDocText(DictionaryItem... dictionaries) {
        StringBuilder docText = new StringBuilder();
        docText.append("== 数据字典");

        if (dictionaries == null || dictionaries.length == 0) {
            return docText.toString();
        }

        for (DictionaryItem dictionary : dictionaries) {
            docText.append("\n\n").append("=== ").append(dictionary.title);
            Enum[] enums = dictionary.type.getEnumConstants();
            if (enums == null || enums.length == 0) {
                continue;
            }

            docText.append("\n").append("|===");
            docText.append("\n").append("| 编码 | 名称");
            try {
                for (Enum dictItem : enums) {
                    Object code = ReflectionUtil.getFieldValue(dictItem, dictionary.codeFieldName);
                    Object msg = ReflectionUtil.getFieldValue(dictItem, dictionary.msgFieldName);
                    docText.append("\n").append("| ").append(code).append(" | ").append(msg);
                }
            } catch (Exception e) {
            }
            docText.append("\n").append("|===");
        }
        return docText.toString();
    }

    private static String generateChildDocText(String docTitle, List<DocItem> docItems) {
        StringBuilder docText = new StringBuilder();
        docText.append("== ").append(docTitle);

        docItems.sort(docItemComparator);
        for (DocItem docItem : docItems) {
            docText.append("\n\n").append("=== ").append(docItem.title);
            docText.append("\n").append("operation::").append(docItem.id).append("[snippets='").append(docItem.snippets).append("']");
        }
        return docText.toString();
    }

    private static boolean ignoreChildDoc(Class<?> testType) {
        AsciidocConfig config = testType.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && config.ignore()) {
            return true;
        }
        return false;
    }

    private static String resolveChildDocId(Class<?> testType) {
        AsciidocConfig config = testType.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && TextUtil.isNotEmpty(config.id())) {
            return config.id();
        }

        String docId = testType.getSimpleName();
        if (docId.endsWith("DocTest")) {
            docId = docId.substring(0, docId.indexOf("DocTest"));
        }
        return camelToMiddlescore(docId);
    }

    private static String resolveChildDocTitle(Class<?> testType) {
        AsciidocConfig config = testType.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && TextUtil.isNotEmpty(config.title())) {
            return config.title();
        }

        String title = testType.getSimpleName();
        if (title.endsWith("DocTest")) {
            title = title.substring(0, title.indexOf("DocTest"));
        }
        return title;
    }

    private static List<DocItem> resolveChildDocItems(Class<?> childDocClass, String childDocId) {
        List<DocItem> childDocItems = new ArrayList<DocItem>();
        Method[] methods = childDocClass.getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return childDocItems;
        }

        for (Method method : methods) {
            if (!isChildDocMethod(method)) {
                continue;
            }

            DocItem childDocItem = new DocItem();
            childDocItem.id = resolveChildDocItemId(method, childDocId);
            childDocItem.title = resolveChildDocItemTitle(method);
            childDocItem.snippets = resolveChildDocItemSnippets(method);
            childDocItem.orderIndex = resolveChildDocItemOrderIndex(method);
            childDocItems.add(childDocItem);
        }
        return childDocItems;
    }

    private static String resolveChildDocItemId(Method method, String docId) {
        AsciidocConfig config = method.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && TextUtil.isNotEmpty(config.id())) {
            return config.id();
        }

        String docItemId = docId + "-" + method.getName();
        return docItemId;
    }

    private static String resolveChildDocItemTitle(Method method) {
        AsciidocConfig config = method.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && TextUtil.isNotEmpty(config.title())) {
            return config.title();
        }

        return method.getName();
    }

    private static String resolveChildDocItemSnippets(Method method) {
        AsciidocConfig config = method.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && TextUtil.isNotEmpty(config.snippets())) {
            return config.snippets();
        }

        return AsciidocConfig.QUERY_PARAMS_SNIPPETS;
    }

    private static int resolveChildDocItemOrderIndex(Method method) {
        AsciidocConfig config = method.getDeclaredAnnotation(AsciidocConfig.class);
        return (config != null ? config.orderIndex() : 0);
    }

    private static boolean isChildDocClass(Class<?> clzz) {
        SpringBootTest springBootTest = clzz.getAnnotation(SpringBootTest.class);
        if (springBootTest == null) {
            return false;
        }

        AsciidocConfig asciidocConfig = clzz.getAnnotation(AsciidocConfig.class);
        if (asciidocConfig != null && asciidocConfig.ignore()) {
            return false;
        }

        return true;
    }

    private static boolean isChildDocMethod(Method method) {
        Test test = method.getDeclaredAnnotation(Test.class);
        if (test == null) {
            return false;
        }

        AsciidocConfig config = method.getDeclaredAnnotation(AsciidocConfig.class);
        if (config != null && config.ignore()) {
            return false;
        }

        return true;
    }

    private static String camelToMiddlescore(String camel) {
        String underscore = TextUtil.camelToUnderscore(camel);
        if (TextUtil.isEmpty(underscore)) {
            return underscore;
        }

        return underscore.replace("_", "-").toLowerCase();
    }

    private static class DocItem {
        String id;

        String title;

        String snippets;

        int orderIndex;
    }

    public static class DictionaryItem {
        private Class<? extends Enum> type;

        private String title;

        private String codeFieldName = "code";

        private String msgFieldName = "msg";

        public DictionaryItem(Class<? extends Enum> type, String title) {
            this(type, title, null, null);
        }

        public DictionaryItem(Class<? extends Enum> type, String title, String codeFieldName, String msgFieldName) {
            Assert.notNull(type, "type must not be null");
            Assert.notNull(title, "title must not be null");
            this.type = type;
            this.title = title;
            if (codeFieldName != null) {
                this.codeFieldName = codeFieldName;
            }
            if (msgFieldName != null) {
                this.msgFieldName = msgFieldName;
            }
        }
    }

    private static final Comparator<Class<?>> docClassComparator = new Comparator<Class<?>>() {

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            AsciidocConfig config1 = o1.getAnnotation(AsciidocConfig.class);
            AsciidocConfig config2 = o2.getAnnotation(AsciidocConfig.class);
            int orderIndex1 = (config1 != null ? config1.orderIndex() : 0);
            int orderIndex2 = (config2 != null ? config2.orderIndex() : 0);
            if (orderIndex1 > orderIndex2) {
                return 1;
            }
            if (orderIndex1 < orderIndex2) {
                return -1;
            }
            return 0;
        }
    };

    private static final Comparator<DocItem> docItemComparator = new Comparator<DocItem>() {
        @Override
        public int compare(DocItem o1, DocItem o2) {
            if (o1.orderIndex > o2.orderIndex) {
                return 1;
            }
            if (o1.orderIndex < o2.orderIndex) {
                return -1;
            }
            return 0;
        }
    };
}

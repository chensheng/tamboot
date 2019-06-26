package com.tamboot.mybatis.provider;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.mybatis.annotation.IgnoreInInsertUpdateSql;
import com.tamboot.mybatis.strategy.InsertStrategy;
import com.tamboot.mybatis.utils.MyBatisAppContextHolder;
import org.springframework.beans.BeansException;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TableInfoResolver {
    private static ConcurrentHashMap<Class<?>, TableInfo> tableInfoCache = new ConcurrentHashMap<Class<?>, TableInfo>();

    public static TableInfo resolve(Class<?> mapperType) {
        if (mapperType == null) {
            return null;
        }

        if (tableInfoCache.containsKey(mapperType)) {
            return tableInfoCache.get(mapperType);
        }

        Class<?> modelType = findModelType(mapperType);
        Field[] modelFields = findFields(modelType);
        Field[] fieldsInSql = findFieldsInSql(modelFields);
        String[] columnsInSql = parseColumnsInSql(fieldsInSql);

        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(parseTableName(modelType));
        tableInfo.setIdColumn(parseIdColumn(modelFields));
        tableInfo.setIdFieldName(parseIdFieldName(modelFields));
        tableInfo.setFieldsInSql(fieldsInSql);
        tableInfo.setColumnsInSql(columnsInSql);
        tableInfoCache.putIfAbsent(mapperType, tableInfo);
        return tableInfo;
    }

    public static String resolveColumn(Field field) {
        if (field == null) {
            return null;
        }

        String column;
        Column columnAnno = field.getAnnotation(Column.class);
        if (columnAnno != null && TextUtil.isNotEmpty(columnAnno.name())) {
            column = columnAnno.name();
        } else {
            column = TextUtil.camelToUnderscore(field.getName()).toLowerCase();
        }
        return column;
    }

    private static String parseTableName(Class<?> modelType) {
        Table annotation = modelType.getDeclaredAnnotation(Table.class);
        if (annotation != null && TextUtil.isNotEmpty(annotation.name())) {
            return annotation.name();
        }

        String modelName = modelType.getSimpleName();
        if (modelName.endsWith("Model")) {
            modelName = modelName.substring(0, modelName.indexOf("Model"));
        } else if (modelName.endsWith("Entity")) {
            modelName = modelName.substring(0, modelName.indexOf("Entity"));
        }

        return TextUtil.camelToUnderscore(modelName).toLowerCase();
    }

    private static String parseIdColumn(Field[] modelFields) {
        Field idField = findIdAnnotationField(modelFields);
        if (idField != null) {
            return resolveColumn(idField);
        }

        return getDefaultIdColumn();
    }

    private static String parseIdFieldName(Field[] modelFields) {
        Field idField = findIdAnnotationField(modelFields);
        if (idField != null) {
            return idField.getName();
        }

        String defaultIdColumn = getDefaultIdColumn();
        return TextUtil.underscoreToCamel(defaultIdColumn);
    }

    private static String[] parseColumnsInSql(Field[] fieldsInSql) {
        String[] columns = new String[fieldsInSql.length];
        for (int i=0; i<fieldsInSql.length; i++) {
            Field field = fieldsInSql[i];
            String column = resolveColumn(field);
            columns[i] = column;
        }
        return columns;
    }

    private static Field findIdAnnotationField(Field[] modelFields) {
        for (Field field : modelFields) {
            Id annotation = field.getAnnotation(Id.class);
            if (annotation != null) {
                return field;
            }
        }
        return null;
    }

    private static Field[] findFieldsInSql(Field[] modelFields) {
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : modelFields) {
            IgnoreInInsertUpdateSql ignoreInSql = field.getAnnotation(IgnoreInInsertUpdateSql.class);
            if (ignoreInSql != null && ignoreInSql.value()) {
                continue;
            }

            fieldList.add(field);
        }

        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    private static Field[] findFields(Class<?> modelType) {
        Field[] declaredFields = modelType.getDeclaredFields();
        List<Field> fieldList = new ArrayList<Field>(declaredFields.length);
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            fieldList.add(field);
        }

        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }

    private static Class<?> findModelType(Class<?> mapperType) {
        return Stream.of(mapperType.getGenericInterfaces())
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .filter(type -> type.getRawType() == CommonMapper.class)
                .findFirst()
                .map(type -> type.getActualTypeArguments()[0])
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .orElseThrow(() -> new IllegalStateException("Could not find CommonMapper's model type for " + mapperType.getName()));
    }

    private static String getDefaultIdColumn() {
        try {
            InsertStrategy insertStrategy = MyBatisAppContextHolder.get().getBean(InsertStrategy.class);
            if (insertStrategy != null && TextUtil.isNotEmpty(insertStrategy.getDefaultIdColumnName())) {
                return insertStrategy.getDefaultIdColumnName();
            }
        } catch (BeansException e) {

        }
        return "id";
    }
}

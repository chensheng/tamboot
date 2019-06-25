package com.tamboot.mybatis.provider;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class CommonSqlProvider {
    public static String insert(ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        return new SQL()
                .INSERT_INTO(tableInfo.getTableName())
                .INTO_COLUMNS(tableInfo.getColumnsInSql())
                .INTO_VALUES(Stream.of(tableInfo.getFieldsInSql()).map(CommonSqlProvider::bindParameter).toArray(String[]::new))
                .toString();
    }

    public static String updateById(ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        return new SQL()
                .UPDATE(tableInfo.getTableName())
                .SET(Stream.of(tableInfo.getFieldsInSql())
                        .filter(field -> !tableInfo.getIdFieldName().equals(field.getName()))
                        .map(field -> TableInfoResolver.resolveColumn(field) + "=" + bindParameter(field))
                        .toArray(String[]::new)
                )
                .WHERE(tableInfo.getIdColumn() + "=" + bindParameterByFieldName(tableInfo.getIdFieldName()))
                .toString();
    }

    public static String updateNotNullById(Object model, ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        return new SQL()
                .UPDATE(tableInfo.getTableName())
                .SET(Stream.of(tableInfo.getFieldsInSql())
                        .filter(field -> !tableInfo.getIdFieldName().equals(field.getName()) && isFieldValueNotNull(model, field))
                        .map(field -> TableInfoResolver.resolveColumn(field) + "=" + bindParameter(field))
                        .toArray(String[]::new)
                )
                .WHERE(tableInfo.getIdColumn() + "=" + bindParameterByFieldName(tableInfo.getIdFieldName()))
                .toString();
    }

    public static String deleteById(ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        return new SQL()
                .DELETE_FROM(tableInfo.getTableName())
                .WHERE(tableInfo.getIdColumn() + "=" + bindParameterByFieldName(tableInfo.getIdFieldName()))
                .toString();
    }

    public static String selectOneById(ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        return new SQL()
                .SELECT("*")
                .FROM(tableInfo.getTableName())
                .WHERE(tableInfo.getIdColumn() + "=" + bindParameterByFieldName(tableInfo.getIdFieldName()))
                .toString();
    }

    public static String selectAllByExample(@Param("example") Object example, @Param("orderBys") String[] orderBys, ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        SQL sql = new SQL().SELECT("*").FROM(tableInfo.getTableName());
        if (example != null) {
            sql.WHERE(Stream.of(tableInfo.getFieldsInSql())
                    .filter(field -> isFieldValueNotNull(example, field))
                    .map(field -> TableInfoResolver.resolveColumn(field) + "=" + bindParameterWithPrefix("example", field))
                    .toArray(String[]::new));
        }
        if (orderBys != null && orderBys.length > 0) {
            sql.ORDER_BY(orderBys);
        }
        return sql.toString();
    }

    public static String pageByExample(@Param("example") Object example, @Param("orderBys") String[] orderBys, ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        SQL sql = new SQL().SELECT("*").FROM(tableInfo.getTableName());
        if (example != null) {
            sql.WHERE(Stream.of(tableInfo.getFieldsInSql())
                    .filter(field -> isFieldValueNotNull(example, field))
                    .map(field -> TableInfoResolver.resolveColumn(field) + "=" + bindParameterWithPrefix("example", field))
                    .toArray(String[]::new));
        }
        if (orderBys != null && orderBys.length > 0) {
            sql.ORDER_BY(orderBys);
        }
        return sql.toString();
    }

    public static String countByExample(Object example, ProviderContext providerContext) {
        TableInfo tableInfo = TableInfoResolver.resolve(providerContext.getMapperType());

        SQL sql = new SQL().SELECT("count(*)").FROM(tableInfo.getTableName());
        if (example != null) {
            sql.WHERE(Stream.of(tableInfo.getFieldsInSql())
                    .filter(field -> isFieldValueNotNull(example, field))
                    .map(field -> TableInfoResolver.resolveColumn(field) + "=" + bindParameter(field))
                    .toArray(String[]::new));
        }
        return sql.toString();
    }

    private static String bindParameter(Field field) {
        return bindParameterByFieldName(field.getName());
    }

    private static String bindParameterWithPrefix(String prefix, Field field) {
        String fieldName = prefix + "." + field.getName();
        return bindParameterByFieldName(fieldName);
    }

    private static String bindParameterByFieldName(String fieldName) {
        return "#{" + fieldName + "}";
    }

    private static boolean isFieldValueNotNull(Object model, Field field) {
        if (model == null) {
            return false;
        }

        try {
            field.setAccessible(true);
            Object value = field.get(model);
            return value != null;
        } catch (IllegalAccessException e) {
        }
        return false;
    }
}

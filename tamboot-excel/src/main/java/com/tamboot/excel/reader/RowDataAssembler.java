package com.tamboot.excel.reader;

import com.tamboot.common.tools.base.ExceptionUtil;
import com.tamboot.common.tools.collection.CollectionUtil;
import com.tamboot.common.tools.reflect.ReflectionUtil;
import com.tamboot.excel.converter.CellContentConverterFactory;
import com.tamboot.excel.core.DataCellConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RowDataAssembler {
    private static final Logger logger = LoggerFactory.getLogger(RowDataAssembler.class);

    public static Object assemble(Class<?> rowType, List<DataCellConfig> dataRowConfig, String[] rowContent, boolean use1904DateWindowing) {
        if (rowType == null) {
            throw new IllegalArgumentException("rowType must not be null");
        }

        Object rowObject = null;
        try {
            rowObject = rowType.newInstance();
        } catch (InstantiationException e) {
            logger.warn(ExceptionUtil.stackTraceText(e));
            return null;
        } catch (IllegalAccessException e) {
            logger.warn(ExceptionUtil.stackTraceText(e));
            return null;
        }

        if (CollectionUtil.isEmpty(dataRowConfig) || rowContent == null || rowContent.length == 0) {
            return rowObject;
        }

        for (DataCellConfig cellConfig : dataRowConfig) {
            if (cellConfig.getField() == null) {
                continue;
            }

            int cellIndex = cellConfig.getIndex();
            if (cellIndex < 0 || cellIndex >= rowContent.length) {
                continue;
            }

            String cellContent = rowContent[cellIndex];
            Object cellValue = CellContentConverterFactory.fromCellContent(cellContent, cellConfig.getField(), cellConfig.getType(), cellConfig.getFormat(), use1904DateWindowing);
            if (cellValue == null) {
                continue;
            }

            try {
                ReflectionUtil.makeAccessible(cellConfig.getField());
                ReflectionUtil.setField(rowObject, cellConfig.getField(), cellValue);
            } catch (Exception e) {
                logger.warn(ExceptionUtil.stackTraceText(e));
            }
        }
        return rowObject;
    }
}

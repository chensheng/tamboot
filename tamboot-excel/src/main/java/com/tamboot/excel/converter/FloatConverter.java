package com.tamboot.excel.converter;

import com.tamboot.excel.core.CellValueType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;

public class FloatConverter implements Converter {
    @Override
    public boolean support(Field field, CellValueType type) {
        Class<?> fieldType = field.getType();
        return Float.class == field.getType() || float.class == field.getType();
    }

    @Override
    public Object fromCellContent(String cellContent, Field field, String format, boolean use1904DateWindowing) {
        Class<?> fieldType = field.getType();
        try {
            return Float.parseFloat(cellContent);
        } catch (NumberFormatException e) {
            return Float.class == field.getType() ? null : 0f;
        }
    }

    @Override
    public void setCellContent(Workbook workbook, Cell cell, Object cellValue, String format) {
        Float value = (Float) cellValue;
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(value);
    }
}

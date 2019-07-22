package com.tamboot.excel.converter;

import com.tamboot.excel.core.CellValueType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class BigDecimalConverter implements Converter {
    @Override
    public boolean support(Field field, CellValueType type) {
        Class<?> fieldType = field.getType();
        return BigDecimal.class == fieldType;
    }

    @Override
    public Object fromCellContent(String cellContent, Field field, String format, boolean use1904DateWindowing) {
        try {
            return new BigDecimal(cellContent);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setCellContent(Workbook workbook, Cell cell, Object cellValue, String format) {
        BigDecimal value = (BigDecimal) cellValue;
        String cellContent = value.toPlainString();
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(cellContent);
    }

}

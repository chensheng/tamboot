package com.tamboot.excel.converter;

import com.tamboot.common.tools.text.TextUtil;
import com.tamboot.excel.core.CellValueType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public class DoubleConverter implements Converter {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[\\+\\-]?[\\d]+([\\.][\\d]*)?([Ee][+-]?[\\d]+)?$");

    @Override
    public boolean support(Field field, CellValueType type) {
        Class<?> fieldType = field.getType();
        return Double.class == fieldType || double.class == fieldType;
    }

    @Override
    public Object fromCellContent(String cellContent, Field field, String format, boolean use1904DateWindowing) {
        Class<?> fieldType = field.getType();

        String text = null;
        if (TextUtil.isEmpty(format)) {
            text = doFormatNumericInNeed(cellContent, null);
        } else {
            int scale = countChar(cellContent, '0');
            text = doFormatNumericInNeed(cellContent, scale);
        }

        if (TextUtil.isEmpty(text)) {
            return Double.class == fieldType ? null : 0d;
        }

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return Double.class == fieldType ? null : 0d;
        }
    }

    @Override
    public void setCellContent(Workbook workbook, Cell cell, Object cellValue, String format) {
        Double value = (Double) cellValue;
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(value);
    }

    public static String doFormatNumericInNeed(String value, Integer scale) {
        if (!value.contains(".") || !isNumeric(value)) {
            return value;
        }

        try {
            BigDecimal bigDecimal = new BigDecimal(value);
            if (scale == null) {
                return bigDecimal.setScale(10, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros().toPlainString();
            } else {
                return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_DOWN).toPlainString();
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int countChar(String value, char targetChar) {
        int count = 0;
        if (value == null) {
            return count;
        }

        char[] chars = value.toCharArray();
        for (char cc : chars) {
            if (cc == targetChar) {
                count++;
            }
        }
        return count;
    }

    private static boolean isNumeric(String str) {
        return NUMERIC_PATTERN.matcher(str).matches();
    }
}

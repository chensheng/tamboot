package com.tamboot.excel;

import com.tamboot.common.tools.collection.CollectionUtil;
import com.tamboot.excel.reader.ExcelReaderFactory;
import com.tamboot.excel.writer.ExcelWriterFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class TambootExcelUtil {
    /**
     * Read excel data from input stream
     * @param inputStream excel data input stream
     * @param rowType row data type
     * @param <T>
     * @return row data list, return null if any argument is null
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> rowType) {
        if (inputStream == null || rowType == null) {
            return null;
        }

        return ExcelReaderFactory.read(inputStream, rowType);
    }

    /**
     * Write data to excel
     * @param outputStream
     * @param rowDataList excel row data list
     */
    public static void write(OutputStream outputStream, List<?> rowDataList) {
        if (outputStream == null || CollectionUtil.isEmpty(rowDataList)) {
            return;
        }
        
        ExcelWriterFactory.write(outputStream, rowDataList);
    }
}

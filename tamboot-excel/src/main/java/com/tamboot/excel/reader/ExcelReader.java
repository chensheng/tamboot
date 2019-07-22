package com.tamboot.excel.reader;

import java.io.InputStream;

public interface ExcelReader {
    void read(InputStream inputStream, RowReadingListener rowReadingListener, Class<?>... rowTypes) throws Exception;
}

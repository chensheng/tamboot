package com.tamboot.excel.reader.xls;

import com.tamboot.excel.core.WorkbookConfig;
import com.tamboot.excel.reader.BaseExcelReader;
import com.tamboot.excel.reader.RowReadingListener;

import java.io.InputStream;

public class XlsReader extends BaseExcelReader {
    @Override
    protected void doRead(InputStream inputStream, RowReadingListener rowReadingListener, WorkbookConfig workbookConfig) throws Exception {
        XlsSheetProcessor processor = new XlsSheetProcessor(rowReadingListener, workbookConfig, false);
        processor.execute(inputStream);
    }
}

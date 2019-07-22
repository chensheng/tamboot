package com.tamboot.excel.reader;

import com.tamboot.excel.core.SheetConfig;

public interface RowReadingListener {
    void onFinish(SheetConfig sheetConfig, Object rowData, int rowIndex);
}

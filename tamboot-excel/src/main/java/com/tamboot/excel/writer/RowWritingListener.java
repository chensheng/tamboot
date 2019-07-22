package com.tamboot.excel.writer;

import com.tamboot.excel.core.SheetConfig;

import java.util.List;

public interface RowWritingListener {
    List<?> getSheetData(SheetConfig sheetConfig);
}

package com.tamboot.excel;

import com.tamboot.common.tools.collection.CollectionUtil;
import com.tamboot.common.tools.text.EscapeUtil;
import com.tamboot.common.tools.text.TextUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TambootServletExcelUtil {
    public static void write(HttpServletResponse response, List<?> models, String fileName) throws IOException {
        if (response == null || CollectionUtil.isEmpty(models) || TextUtil.isEmpty(fileName)) {
            return;
        }

        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + EscapeUtil.urlEncode(fileName) + ".xlsx");

        TambootExcelUtil.write(outputStream, models);
        outputStream.flush();
    }
}

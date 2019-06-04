package com.tamboot.webapp.core;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;

public class PageAdapter implements Serializable {
    private int pageNum;

    private int pageSize;

    private long total;

    private int pages;

    private List<?> result;

    public PageAdapter() {
    }

    public PageAdapter(Page<?> page) {
        if (page != null) {
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.total = page.getTotal();
            this.pages = page.getPages();
            this.result = page.getResult();
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }
}

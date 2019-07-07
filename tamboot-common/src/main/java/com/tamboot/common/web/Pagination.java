package com.tamboot.common.web;

import java.io.Serializable;
import java.util.List;

public class Pagination<T> implements Serializable {
    private int pageNum;

    private int pageSize;

    private long total;

    private int pages;

    private List<T> result;

    public Pagination() {
    }

    public Pagination(int pageNum, int pageSize, long total, int pages, List<T> result) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = pages;
        this.result = result;
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

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}

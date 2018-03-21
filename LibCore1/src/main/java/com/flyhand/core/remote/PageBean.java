package com.flyhand.core.remote;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 12-4-21
 * Time: Afternoon 7:00
 */
public class PageBean<T> {
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer totalNum = 0;
    private Integer totalPage = 0;
    private List<T> list;

    public PageBean() {
    }

    public PageBean(List<T> list, Integer totalNum, Integer pageIndex) {
        this.list = list;
        totalPage = totalNum / pageSize;
        if (totalNum % pageSize != 0) {
            totalPage += 1;
        }
        this.totalNum = totalNum;
        this.pageIndex = pageIndex;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean hasList() {
        return null != list && list.size() > 0;
    }

    public boolean hasNextPage() {
        return pageIndex < totalPage;
    }
}

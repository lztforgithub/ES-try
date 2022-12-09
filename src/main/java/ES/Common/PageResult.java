package ES.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PageResult<T> implements Iterable<T>{

    /**
     * 数据条数
     */
    private List<T> list = new ArrayList<>();

    /**
     * 一共有多少条数据
     */
    private long total;

    /**
     * 当前页数
     */
    private long currentPage;

    /**
     * 一页数据最多有多少条数据
     */
    private long pageSize;

    /**
     * 上限有多少页，用于限制显示数据条数
     */
    private long pageNum;

    /**
     * 当前数据有多少页
     */
    private long totalPage;

    /**
     * 当前页数有多少条数据
     */
    private int numberOfElements;

    /**
     * 是否有前一页
     *
     * @return boolean
     */
    public boolean hasPrevious() {
        return getCurrentPage() > 0;
    }

    /**
     * 是否有下一页
     *
     * @return boolean
     */
    public boolean hasNext() {
        return getCurrentPage() + 1 < getPageNum();
    }

    /**
     * 获取上限页数
     *
     * @return long
     */
    public long getPageNum() {
        return this.pageNum;
    }

    /**
     * 设置上限的页数
     *
     * @param pageNum  int
     */
    public long setPageNum(int pageNum) {
        return this.pageNum = pageNum;
    }

    /**
     * 获取当前页数
     *
     * @return long
     */
    public long getTotalPage() {
        return this.totalPage;
    }

    /**
     * 设置上限的页数
     *
     * @param totalPage int
     */
    public long setTotalPage(int totalPage) {
        return this.totalPage = totalPage;
    }


    /**
     * 获取当前页的数据
     *
     * @return List<T>
     */
    public List<T> getList() {
        return Collections.unmodifiableList(list);
    }

    /**
     * 设置内容
     *
     * @param list 内容
     */
    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * 是否有内容
     *
     * @return boolean
     */
    public boolean hasRecords() {
        return getNumberOfElements() > 0;
    }

    /**
     * 获取单页大小
     */
    public Long getPageSize() {
        return pageSize;
    }

    /**
     * 设置单页大小
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取全部元素数目
     */
    public long getTotal() {
        return total;
    }

    /**
     * 设置全部元素数目
     */
    public void setTotal(long total) {
        this.total = total;
    }


    /**
     * 获取当前页号
     */
    public Long getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页号
     */
    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }


    /**
     * 获取单页元素数目
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * 设置单页元素数目
     */
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }


    /**
     * 迭代器
     */
    @Override
    public Iterator<T> iterator() {
        return getList().iterator();
    }

}

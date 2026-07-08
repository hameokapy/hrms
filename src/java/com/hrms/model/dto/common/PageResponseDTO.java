
package com.hrms.model.dto.common;

import java.io.Serializable;
import java.util.List;

public class PageResponseDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<T> content;      
    private int totalPages;      
    private long totalElements;   
    private int currentPage;     
    private int pageSize;        

    public PageResponseDTO() {
    }

    public PageResponseDTO(List<T> content, long totalElements, int currentPage, int pageSize) {
        this.content = content;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    
}

package com.mnf.javaapigenerator.component.dto;

public class PaginationInfoDto {
    private Integer currentPage;
    private Integer totalPages;
    private Integer size;
    private Integer totalItems;
    private String sortBy;
    private String sortType;

    public PaginationInfoDto(Integer currentPage, Integer totalPages, Integer size, Integer totalItems, String sortBy, String sortType) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.size = size;
        this.totalItems = totalItems;
        this.sortBy = sortBy;
        this.sortType = sortType;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}

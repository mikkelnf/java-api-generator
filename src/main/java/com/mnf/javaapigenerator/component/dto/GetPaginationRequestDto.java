package com.mnf.javaapigenerator.component.dto;

public class GetPaginationRequestDto<T> {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortType;
    private T dto;

    public GetPaginationRequestDto() {
    }

    public GetPaginationRequestDto(Integer page, Integer size, String sortBy, String sortType, T dto) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortType = sortType;
        this.dto = dto;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
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

    public T getDto() {
        return dto;
    }

    public void setDto(T dto) {
        this.dto = dto;
    }
}

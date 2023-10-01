package com.mnf.javaapigenerator.component.dto;

import java.util.List;

public class GetPaginationResponseDto<T> {
    private List<T> results;
    private PaginationInfoDto paginationInfo;

    public GetPaginationResponseDto(List<T> results, PaginationInfoDto paginationInfo) {
        this.results = results;
        this.paginationInfo = paginationInfo;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public PaginationInfoDto getPaginationInfo() {
        return paginationInfo;
    }

    public void setPaginationInfo(PaginationInfoDto paginationInfo) {
        this.paginationInfo = paginationInfo;
    }
}

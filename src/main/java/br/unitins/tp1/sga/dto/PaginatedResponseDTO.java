package br.unitins.tp1.sga.dto;

import java.util.List;

public class PaginatedResponseDTO<T> {
    public List<T> content;
    public long totalElements;
    public int totalPages;
    public int currentPage;
    public int pageSize;
    public boolean hasNext;
    public boolean hasPrevious;

    public PaginatedResponseDTO(List<T> content, long totalElements, int currentPage, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        this.hasNext = currentPage + 1 < totalPages;
        this.hasPrevious = currentPage > 0;
    }
}

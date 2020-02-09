package com.softwarfare.biothenticate.api.models;

import lombok.Data;

@Data
public class PaginatedList<T> {

    private T[] content;
    private boolean empty;
    private int pageElements;
    private int pageNumber;
    private int pageSize;
    private int totalElements;
    private int totalPages;

}

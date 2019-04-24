package ee.hm.dop.model.administration;

import ee.hm.dop.model.Searchable;

import java.util.List;

public class DopPage<T extends Searchable> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private long totalElements;

    public DopPage() {
    }

    public DopPage(List<T> content, Integer page, Integer size, Integer totalPages, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
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

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

}

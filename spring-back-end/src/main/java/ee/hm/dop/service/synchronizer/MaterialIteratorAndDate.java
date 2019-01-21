package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.synchronizer.oaipmh.MaterialIterator;

import java.time.LocalDateTime;

public class MaterialIteratorAndDate {

    private MaterialIterator iterator;
    private LocalDateTime syncDate;

    public MaterialIteratorAndDate(MaterialIterator iterator, LocalDateTime syncDate) {
        this.iterator = iterator;
        this.syncDate = syncDate;
    }

    public MaterialIterator getIterator() {
        return iterator;
    }

    public void setIterator(MaterialIterator iterator) {
        this.iterator = iterator;
    }

    public LocalDateTime getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(LocalDateTime syncDate) {
        this.syncDate = syncDate;
    }
}

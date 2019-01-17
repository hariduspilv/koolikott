package ee.hm.dop.service.synchronizer;

import ee.hm.dop.service.synchronizer.oaipmh.MaterialIterator;
import org.joda.time.DateTime;

public class MaterialIteratorAndDate {

    private MaterialIterator iterator;
    private DateTime syncDate;

    public MaterialIteratorAndDate(MaterialIterator iterator, DateTime syncDate) {
        this.iterator = iterator;
        this.syncDate = syncDate;
    }

    public MaterialIterator getIterator() {
        return iterator;
    }

    public void setIterator(MaterialIterator iterator) {
        this.iterator = iterator;
    }

    public DateTime getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(DateTime syncDate) {
        this.syncDate = syncDate;
    }
}

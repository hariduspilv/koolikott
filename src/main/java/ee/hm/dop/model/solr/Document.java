package ee.hm.dop.model.solr;

public class Document {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

}

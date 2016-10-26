package ee.hm.dop.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Thumbnail")
public class Thumbnail extends Picture {

    public Thumbnail() {}

}

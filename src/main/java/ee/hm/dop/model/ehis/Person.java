package ee.hm.dop.model.ehis;

import java.util.List;

public class Person {

    private List<Institution> institutions;

    public List<Institution> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }
}

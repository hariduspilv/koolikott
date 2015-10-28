package ee.hm.dop.model;

public class SearchFilter {

    private String educationalContext;

    private boolean paid = true;

    private String type;

    public String getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(String educationalContext) {
        this.educationalContext = educationalContext;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}

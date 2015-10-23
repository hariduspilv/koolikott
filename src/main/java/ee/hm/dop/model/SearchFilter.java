package ee.hm.dop.model;

public class SearchFilter {

    private String subject;

    private String resourceType;

    private String educationalContext;

    private String licenseType;

    private String author;

    private String combinedDescription;

    private boolean paid = true;

    private String type;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getEducationalContext() {
        return educationalContext;
    }

    public void setEducationalContext(String educationalContext) {
        this.educationalContext = educationalContext;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCombinedDescription() {
        return combinedDescription;
    }

    public void setCombinedDescription(String description) {
        this.combinedDescription = description;
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

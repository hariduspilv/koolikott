package ee.hm.dop.model;

import javax.persistence.*;

@Entity
@Table(name = "Picture")
public class OriginalPicture extends Picture {

    @ManyToOne
    @JoinColumn(name = "licenseType")
    private LicenseType licenseType;

    @Column
    private String author;

    @Column
    private String source;

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

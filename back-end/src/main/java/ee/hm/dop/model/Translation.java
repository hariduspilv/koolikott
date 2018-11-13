package ee.hm.dop.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Translation implements AbstractEntity {

    public Translation() {
    }

    public Translation(long translationGroup, String translationKey, String translation) {
        this.translationGroup = translationGroup;
        this.translationKey = translationKey;
        this.translation = translation;
    }

    private long translationGroup;

    @Id
    private String translationKey;

    @Column(columnDefinition = "TEXT")
    private String translation;


    public long getTranslationGroup() {
        return translationGroup;
    }

    public void setTranslationGroup(long translationGroup) {
        this.translationGroup = translationGroup;
    }


    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public Long getId() {
        return null;
    }
}

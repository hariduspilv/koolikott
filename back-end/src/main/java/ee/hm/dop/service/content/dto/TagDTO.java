package ee.hm.dop.service.content.dto;

import ee.hm.dop.model.LearningObject;

public class TagDTO {

    private LearningObject learningObject;

    private String tagTypeName;

    public TagDTO() {}

    public TagDTO(LearningObject learningObject, String tagTypeName) {
        this.learningObject = learningObject;
        this.tagTypeName = tagTypeName;
    }

    public LearningObject getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(LearningObject learningObject) {
        this.learningObject = learningObject;
    }

    public String getTagTypeName() {
        return tagTypeName;
    }

    public void setTagTypeName(String tagTypeName) {
        this.tagTypeName = tagTypeName;
    }
}

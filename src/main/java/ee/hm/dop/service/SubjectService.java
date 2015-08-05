package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.SubjectDAO;
import ee.hm.dop.model.Subject;

public class SubjectService {

    @Inject
    private SubjectDAO subjectDAO;

    public List<Subject> getAllSubjects() {
        return subjectDAO.findAll();
    }

}

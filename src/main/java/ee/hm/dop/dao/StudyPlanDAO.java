package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import ee.hm.dop.model.StudyPlan;

public class StudyPlanDAO {

    @Inject
    private EntityManager entityManager;

    public StudyPlan findById(long studyPlanId) {
        return entityManager.find(StudyPlan.class, studyPlanId);
    }
}

package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.StudyPlanDAO;
import ee.hm.dop.model.StudyPlan;

public class StudyPlanService {

    @Inject
    private StudyPlanDAO studyPlanDAO;

    public StudyPlan get(long materialId) {
        return studyPlanDAO.findById(materialId);
    }
}

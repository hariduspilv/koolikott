package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.CrossCurricularThemeDAO;
import ee.hm.dop.model.CrossCurricularTheme;

public class CrossCurricularThemeService {

    @Inject
    private CrossCurricularThemeDAO crossCurricularThemeDAO;

    public CrossCurricularTheme getCrossCurricularThemeById(Long id) {
        return crossCurricularThemeDAO.findCrossCurricularThemeById(id);
    }

    public List<CrossCurricularTheme> getAllCrossCurricularThemes() {
        return crossCurricularThemeDAO.findAll();
    }

}
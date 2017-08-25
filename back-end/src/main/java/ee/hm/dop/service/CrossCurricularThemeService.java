package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.CrossCurricularThemeDao;
import ee.hm.dop.model.CrossCurricularTheme;

public class CrossCurricularThemeService {

    @Inject
    private CrossCurricularThemeDao crossCurricularThemeDao;

    public CrossCurricularTheme getCrossCurricularThemeById(Long id) {
        return crossCurricularThemeDao.findById(id);
    }

    public List<CrossCurricularTheme> getAllCrossCurricularThemes() {
        return crossCurricularThemeDao.findAll();
    }

    public CrossCurricularTheme getThemeByName(String name) {
        return crossCurricularThemeDao.findByName(name);
    }

}
package ee.hm.dop.dao;

import java.util.List;

import ee.hm.dop.model.CrossCurricularTheme;

public class CrossCurricularThemeDAO extends BaseDAO {

    public List<CrossCurricularTheme> findAll() {
        return createQuery("from CrossCurricularTheme", CrossCurricularTheme.class).getResultList();
    }

}
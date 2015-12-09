package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.CrossCurricularTheme;

public class CrossCurricularThemeDAO extends BaseDAO {

    public CrossCurricularTheme findCrossCurricularThemeById(Long id) {
        TypedQuery<CrossCurricularTheme> findById = createQuery("FROM CrossCurricularTheme c WHERE c.id = :id",
                CrossCurricularTheme.class).setParameter("id", id);

        return getSingleResult(findById);
    }

    public List<CrossCurricularTheme> findAll() {
        return createQuery("from CrossCurricularTheme", CrossCurricularTheme.class).getResultList();
    }

}
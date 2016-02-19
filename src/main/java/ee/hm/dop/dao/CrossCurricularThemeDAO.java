package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.CrossCurricularTheme;

public class CrossCurricularThemeDAO extends BaseDAO<CrossCurricularTheme> {

    public CrossCurricularTheme findCrossCurricularThemeById(Long id) {
        TypedQuery<CrossCurricularTheme> findById = createQuery("FROM CrossCurricularTheme c WHERE c.id = :id",
                CrossCurricularTheme.class).setParameter("id", id);

        return getSingleResult(findById);
    }

    public List<CrossCurricularTheme> findAll() {
        return createQuery("from CrossCurricularTheme", CrossCurricularTheme.class).getResultList();
    }

    public CrossCurricularTheme getThemeByName(String name) {
        TypedQuery<CrossCurricularTheme> findByName = createQuery("FROM CrossCurricularTheme c WHERE c.name = :name",
                CrossCurricularTheme.class).setParameter("name", name);

        return getSingleResult(findByName);
    }
}
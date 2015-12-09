package ee.hm.dop.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.KeyCompetence;

public class KeyCompetenceDAO extends BaseDAO {

    public KeyCompetence findKeyCompetenceById(Long id) {
        TypedQuery<KeyCompetence> findById = createQuery("FROM KeyCompetence k WHERE k.id = :id", KeyCompetence.class)
                .setParameter("id", id);

        return getSingleResult(findById);
    }

    public List<KeyCompetence> findAll() {
        return createQuery("from KeyCompetence", KeyCompetence.class).getResultList();
    }

}
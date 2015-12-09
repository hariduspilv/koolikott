package ee.hm.dop.dao;

import java.util.List;

import ee.hm.dop.model.KeyCompetence;

public class KeyCompetenceDAO extends BaseDAO {

    public List<KeyCompetence> findAll() {
        return createQuery("from KeyCompetence", KeyCompetence.class).getResultList();
    }

}
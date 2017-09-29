package ee.hm.dop.service.metadata;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.KeyCompetenceDao;
import ee.hm.dop.model.KeyCompetence;

public class KeyCompetenceService {

    @Inject
    private KeyCompetenceDao keyCompetenceDao;

    public KeyCompetence getKeyCompetenceById(Long id) {
        return keyCompetenceDao.findById(id);
    }

    public List<KeyCompetence> getKeyCompetenceById(List<Long> id) {
        return keyCompetenceDao.findById(id);
    }

    public List<KeyCompetence> getAllKeyCompetences() {
        return keyCompetenceDao.findAll();
    }

    public KeyCompetence findKeyCompetenceByName(String name) {
        return keyCompetenceDao.findByName(name);
    }
}
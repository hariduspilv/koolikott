package ee.hm.dop.service.metadata;

import ee.hm.dop.dao.KeyCompetenceDao;
import ee.hm.dop.model.KeyCompetence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
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

    public List<KeyCompetence> findKeyCompetenceByName(List<String> name) {
        return keyCompetenceDao.findByName(name);
    }
}
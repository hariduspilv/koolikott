package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.KeyCompetenceDAO;
import ee.hm.dop.model.KeyCompetence;

public class KeyCompetenceService {

    @Inject
    private KeyCompetenceDAO keyCompetenceDAO;

    public KeyCompetence getKeyCompetenceById(Long id) {
        return keyCompetenceDAO.findKeyCompetenceById(id);
    }

    public List<KeyCompetence> getAllKeyCompetences() {
        return keyCompetenceDAO.findAll();
    }

    public KeyCompetence findKeyCompetenceByName(String name) {
        return keyCompetenceDAO.findKeyCompetenceByName(name);
    }
}
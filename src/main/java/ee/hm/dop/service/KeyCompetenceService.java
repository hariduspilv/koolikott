package ee.hm.dop.service;

import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.KeyCompetenceDAO;
import ee.hm.dop.model.KeyCompetence;

public class KeyCompetenceService {

    @Inject
    private KeyCompetenceDAO keyCompetenceDAO;

    public List<KeyCompetence> getAllKeyCompetences() {
        return keyCompetenceDAO.findAll();
    }

}
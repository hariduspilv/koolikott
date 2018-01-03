package ee.hm.dop.dao.specialized;

import ee.hm.dop.service.reviewmanagement.dto.StatisticSearchFilterDto;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class StatisticDao {
    @Inject
    private EntityManager entityManager;

    public void xyz(StatisticSearchFilterDto dto) {
        entityManager.createNativeQuery("").getResultList();
    }
}

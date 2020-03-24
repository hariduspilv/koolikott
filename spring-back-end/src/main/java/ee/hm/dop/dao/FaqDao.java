package ee.hm.dop.dao;

import ee.hm.dop.model.Faq;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FaqDao extends AbstractDao<Faq> {

    public List<Faq> getOrderedFaqs() {
        return getEntityManager()
                .createQuery("SELECT f FROM Faq f ORDER BY f.faqOrder ASC", Faq.class)
                .getResultList();
    }

}

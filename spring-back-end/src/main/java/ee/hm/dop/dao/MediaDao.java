package ee.hm.dop.dao;

import ee.hm.dop.model.Media;
import ee.hm.dop.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MediaDao extends AbstractDao<Media>{

    public List<Media> findAllByCreator(User creator) {
        return getEntityManager().createQuery("SELECT m FROM Media m " +
                "WHERE m.createdBy = :creator", entity())
                .setParameter("creator", creator)
                .getResultList();
    }
}

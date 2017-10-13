package ee.hm.dop.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import org.apache.commons.collections.CollectionUtils;

public class TaxonDao extends AbstractDao<Taxon> {

    public static final String EDUCATIONAL_CONTEXT = "EDUCATIONAL_CONTEXT";

    public Taxon findContextByName(String name, String level) {
        TypedQuery<Taxon> findByName = getEntityManager()
                .createQuery("FROM Taxon t WHERE lower(t.name) = :name and level = :level", entity()) //
                .setParameter("name", name.toLowerCase())
                .setParameter("level", level);
        return getSingleResult(findByName);
    }

    public List<EducationalContext> findAllEducationalContext() {
        return getEntityManager()
                .createQuery("SELECT e FROM EducationalContext e", EducationalContext.class)
                .getResultList();
    }

    public Taxon findTaxonByName(String name) {
        return getSingleResult(getEntityManager()
                .createQuery("SELECT t FROM Taxon t WHERE lower(t.name)=:name", entity())
                .setParameter("name", name.toLowerCase())
                .setMaxResults(1));
    }

    public Taxon findTaxonByRepoName(String name, String repoTable, Class<? extends Taxon> level) {
        List<Taxon> taxons = getEntityManager()
                .createQuery("SELECT t.taxon FROM " + repoTable + " t WHERE lower(t.name) = :name",
                entity()).setParameter("name", name.toLowerCase()).getResultList();
        List<Taxon> res = taxons.stream()
                .filter(t -> level.isAssignableFrom(t.getClass()))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(res)) {
            return res.get(0);
        }
        return null;
    }
}

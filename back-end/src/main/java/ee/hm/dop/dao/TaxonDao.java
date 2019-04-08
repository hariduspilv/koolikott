package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.TaxonPosition;
import ee.hm.dop.utils.TaxonUtils;
import ee.hm.dop.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TaxonDao extends AbstractDao<Taxon> {
    private final Logger logger = LoggerFactory.getLogger(TaxonDao.class);


    public List<Taxon> findTaxonDomainAndSubject(Taxon taxon) {
        return getEntityManager()
                .createNativeQuery("" +
                        "  SELECT tp.domain as t_id FROM TaxonPosition tp\n" +
                        "  WHERE tp.taxon= :taxonId\n" +
                        "  UNION ALL\n" +
                        "  SELECT tp.subject as t_id FROM TaxonPosition tp\n" +
                        "  WHERE tp.taxon= :taxonId ")
                .setParameter("taxonId", taxon.getId())
                .getResultList();
    }


    public List<EducationalContext> findAllEducationalContext() {
        return getEntityManager()
                .createQuery("SELECT e FROM EducationalContext e", EducationalContext.class)
                .getResultList();
    }

    public Taxon findTaxonByName(String name) {
        return getSingleResult(getEntityManager()
                .createQuery("SELECT t FROM Taxon t WHERE t.nameLowercase=:name and t.used = 1", entity())
                .setParameter("name", name.toLowerCase())
                .setMaxResults(1));
    }

    public Taxon findTaxonByEstCoreName(String name, Class<? extends Taxon> level) {
        List<Taxon> taxons = findTaxonsByEstCoreName(name, level);

        if (taxons.isEmpty()) {
            return null;
        }
        if (taxons.size() == 1) {
            return taxons.get(0);
        }
        String ids = taxons.stream().map(Taxon::getId).map(Object::toString).collect(Collectors.joining(", "));
        logger.error(String.format("Found multiple taxons for parameters: name - %s, level - %s, ids - %s",
                ids, name, level.getSimpleName()));
        return taxons.get(0);
    }

    public List<Taxon> findTaxonsByEstCoreName(String name, Class<? extends Taxon> level) {
        List<Taxon> taxons = getEntityManager()
                .createQuery("SELECT t.taxon FROM EstCoreTaxonMapping t WHERE t.nameLowercase = :name", entity())
                .setParameter("name", name.toLowerCase())
                .getResultList();
        return taxons.stream()
                .filter(t -> level.isAssignableFrom(t.getClass()))
                .collect(Collectors.toList());
    }

    public List<Taxon> getUserTaxons(User user) {
        UserUtil.mustBeModerator(user);
        //i am sorry i tried very hard
        Collection userTaxons = getEntityManager().createQuery("SELECT t.userTaxons \n" +
                "FROM User t\n" +
                "WHERE t.id = :userId", Collection.class)
                .setParameter("userId", user.getId())
                .getResultList();


        List<Taxon> taxons = new ArrayList<>();
        for (Object userTaxon : userTaxons) {
            taxons.add((Taxon) userTaxon);
        }

        return taxons;
    }

    /**
     * when user is assigned high level taxon
     * then they can access all of its children
     */
    public List<Long> getUserTaxonsWithChildren(User user) {
        UserUtil.mustBeModerator(user);
        //i am sorry i tried very hard
        Collection userTaxons = getEntityManager().createQuery("SELECT t.userTaxons \n" +
                "FROM User t\n" +
                "WHERE t.id = :userId", Collection.class)
                .setParameter("userId", user.getId())
                .getResultList();


        List<Taxon> taxons = new ArrayList<>();
        for (Object userTaxon : userTaxons) {
            taxons.add((Taxon) userTaxon);
        }

        return taxons.stream()
                .map(this::getTaxonWithChildren)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Long> getTaxonWithChildren(Taxon taxon) {
        List<String> tree = TaxonUtils.getLevelTree(taxon);
        return getTaxonWithChildren(Arrays.asList(taxon.getId()), tree);
    }

    /**
     * when user is assigned high level taxon
     * then they can access all of its children
     */
    private List<Long> getTaxonWithChildren(List<Long> ids, List<String> levels) {
        List<BigInteger> resultList = (List<BigInteger>) getEntityManager()
                .createNativeQuery("SELECT DISTINCT taxon.id\n" +
                        "FROM EducationalContext educationalContext\n" +
                        "  LEFT JOIN Domain domain ON domain.educationalContext = educationalContext.id\n" +
                        "  LEFT JOIN Specialization specialization ON specialization.domain = domain.id\n" +
                        "  LEFT JOIN Module module ON module.specialization = specialization.id\n" +
                        "  LEFT JOIN Subject subject ON subject.domain = domain.id\n" +
                        "  LEFT JOIN Topic topic ON topic.domain = domain.id\n" +
                        "                           OR topic.module = module.id\n" +
                        "                           OR topic.subject = subject.id\n" +
                        "  LEFT JOIN Subtopic subtopic ON subtopic.topic = topic.id\n" +
                        "  JOIN Taxon taxon ON taxon.id = educationalContext.id\n" +
                        "                    OR taxon.id = domain.id\n" +
                        "                    OR taxon.id = specialization.id\n" +
                        "                    OR taxon.id = module.id\n" +
                        "                    OR taxon.id = subject.id\n" +
                        "                    OR taxon.id = topic.id\n" +
                        "                    OR taxon.id = subtopic.id\n" +
                        "WHERE (educationalContext.id IN (:taxonIds)\n" +
                        "       OR domain.id IN (:taxonIds)\n" +
                        "       OR specialization.id IN (:taxonIds)\n" +
                        "       OR module.id IN (:taxonIds)\n" +
                        "       OR subject.id IN (:taxonIds)\n" +
                        "       OR topic.id IN (:taxonIds)\n" +
                        "       OR subtopic.id IN (:taxonIds))\n" +
                        "      AND taxon.level IN (:levels)")
                .setParameter("taxonIds", ids)
                .setParameter("levels", levels)
                .getResultList();

        return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }

    public List<Long> getUserTaxonWithChildren(Long user) {
        return getUserTaxonWithChildren(Arrays.asList(user));
    }

    /**
     * when user is assigned high level taxon
     * then they can access all of its children
     */
    public List<Long> getUserTaxonWithChildren(List<Long> users) {
        List<BigInteger> resultList = (List<BigInteger>) getEntityManager()
                .createNativeQuery("SELECT TP1.taxon \n" +
                        " FROM User_Taxon ut,TaxonPosition TP1\n" +
                        " WHERE ut.user = :users \n" +
                        " AND (ut.taxon = TP1.educationalContext\n" +
                        "OR ut.taxon = TP1.domain\n" +
                        "OR ut.taxon = TP1.subject\n" +
                        "OR ut.taxon = TP1.module\n" +
                        "OR ut.taxon = TP1.specialization\n" +
                        "OR ut.taxon = TP1.topic\n" +
                        "OR ut.taxon = TP1.subtopic)\n" +
                        "GROUP BY taxon")
                .setParameter("users", users)
                .getResultList();
        return resultList.stream().map(BigInteger::longValue).collect(Collectors.toList());
    }
}

package ee.hm.dop.dao;

import ee.hm.dop.model.User;
import ee.hm.dop.model.administration.PageableQueryUsers;
import ee.hm.dop.model.enums.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

@Repository
public class UserDao extends AbstractDao<User> {

    public User findUserById(Long id) {
        return findById(id);
    }

    public User findUserByIdCode(String idCode) {
        return findByField("idCode", idCode);
    }

    public User findUserByUsername(String username) {
        return findByField("username", username);
    }

    /**
     * Counts the amount of users who have the same username, excluding the
     * number at the end. For example, users <i>john.smith</i> and
     * <i>john.smith2</i> are considered to have the same username.
     * username
     *
     * @param username the username to search for
     * @return the count of users with the same username, excluding the number
     */
    public Long countUsersWithSameUsername(String username) {
        BigInteger count = (BigInteger) entityManager.createNativeQuery("select count(userName) from User where userName REGEXP :username")
                .setParameter("username", username + "[0-9]*$")
                .getSingleResult();
        return count.longValue();
    }

    public void delete(User user) {
        entityManager.remove(user);
    }

    public List<User> getUsersByRole(Role role) {
        return findByFieldList("role", role);
    }

    public Long getUsersCountByRole(Role role) {
        return (Long) getCountByField("role", role);
    }

    public Long getAllUsersCount() {
        String sql = "select count(u.id) from User u";
        Query query = entityManager.createNativeQuery(sql);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public Long getUsersCount(PageableQueryUsers params) {
        String sql = "select count(distinct u.id) from User u " +
                "left join UserEmail ue on u.id = ue.user " +
                "left join UserProfile up on u.id = up.user " +
                "left join User_InterestTaxon uit on u.id = uit.user " +
                "left join TaxonPosition tp on uit.taxon = tp.taxon " +
                "left join Taxon t on tp.educationalContext = t.id " +
                "left join AuthenticatedUser au on u.id = au.user_id " +
                "left join Translation tl on tl.translationGroup = :translationGroup and u.role = tl.translationKey " +
                "left join Translation tl2 on tl2.translationGroup = :translationGroup and CONCAT('PROFILE_', up.role) = tl2.translationKey " +
                "where 1=1 ";
        sql = addConditions(sql, params);
        Query query = addParams(entityManager.createNativeQuery(sql), params);
        return ((BigInteger) query.getSingleResult()).longValue();
    }

    public List<Object[]> findAllUsers(PageableQueryUsers params) {
        String sql = "select max(u.id), max(u.name), max(u.surName), max(ue.email), max(ue.activated), max(up.customRole), max(tl.translation), max(tl2.translation), max(au.loginDate), " +
                "group_concat(distinct tl3.translation separator ';'), group_concat(distinct tl4.translation separator ';') from User u " +
                "left join UserEmail ue on u.id = ue.user " +
                "left join UserProfile up on u.id = up.user " +
                "left join User_InterestTaxon uit on u.id = uit.user " +
                "left join TaxonPosition tp on uit.taxon = tp.taxon " +
                "left join Taxon t on tp.educationalContext = t.id " +
                "left join Taxon t2 on tp.domain = t2.id " +
                "left join AuthenticatedUser au on u.id = au.user_id " +
                "left join Translation tl on tl.translationGroup = :translationGroup and u.role = tl.translationKey " +
                "left join Translation tl2 on tl2.translationGroup = :translationGroup and CONCAT('PROFILE_', up.role) = tl2.translationKey " +
                "left join Translation tl3 on tl3.translationGroup = :translationGroup and t.name = tl3.translationKey " +
                "left join Translation tl4 on tl4.translationGroup = :translationGroup and t2.translationKey = tl4.translationKey " +
                "where 1=1 ";

        sql = addConditions(sql, params);
        sql += params.group() + params.order();
        Query query = addParams(entityManager.createNativeQuery(sql), params);
        query.setFirstResult(params.getOffset()).setMaxResults(params.getSize());

        return query.getResultList();
    }

    private String addConditions(String sql, PageableQueryUsers params) {
        if (!StringUtils.isEmpty(params.getQuery())) {
            sql = sql + "and ((LOWER(u.name) like :queryString) or (LOWER(u.surName) like :queryString) " +
                    "or (CONCAT(LOWER(u.name), ' ', LOWER(u.surName)) like :queryString)) ";
        }
        if (params.getRole() != null) {
            sql = sql + "and u.role = :role ";
        }
        if (params.getUserRole() != null) {
            sql = sql + "and up.role = :userRole ";
        }
        if (params.isWithEmail()) {
            sql = sql + "and ue.email is not null ";
        }
        if (params.isWithoutEmail()) {
            sql = sql + "and ue.email is null ";
        }
        if (params.getUserEducationalContext() != null) {
            sql += "and t.name = :educationalContext ";
        }
        return sql;
    }

    private Query addParams(Query query, PageableQueryUsers params) {
        if (!StringUtils.isEmpty(params.getQuery())) {
            query.setParameter("queryString", "%" + params.getQuery() + "%");
        }
        if (params.getRole() != null) {
            query.setParameter("role", params.getRole());
        }
        if (params.getUserRole() != null) {
            query.setParameter("userRole", params.getUserRole());
        }
        if (params.getUserEducationalContext() != null) {
            query.setParameter("educationalContext", params.getUserEducationalContext());
        }
        if (params.getLanguageCode() != null) {
            query.setParameter("translationGroup", getCorrectTranslationGroup(params.getLanguageCode()));
        } else {
            query.setParameter("translationGroup", 1);
        }
        return query;
    }

    private Integer getCorrectTranslationGroup(String languageCode) {
        switch (languageCode) {
            case "est":
                return 1;
            case "rus":
                return 2;
            case "eng":
                return 3;
            default:
                return null;
        }
    }
}

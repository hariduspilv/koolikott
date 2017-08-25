package ee.hm.dop.dao;

import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.Deletable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractDao<Entity extends AbstractEntity> {

    public static final String ALIAS = " o ";
    public static final String WHERE = " where ";
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String ID = "id";
    public static final String SET_DELETED_TRUE = " set o.deleted = true ";
    public static final String UPDATE = "update ";
    @Inject
    protected EntityManager entityManager;
    private Class<Entity> entity;

    @Inject
    public void postConstruct() {
        entity = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<Entity> entity() {
        return entity;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public Entity findById(Long id) {
        return getEntityManager().find(entity(), id);
    }

    public List<Entity> findById(List<Long> id) {
        return getList(getFindByFieldInQuery(ID, id));
    }

    public List<Entity> findAll() {
        return getList(getEntityManager().createQuery(select(), entity()));
    }

    public Entity findByName(String value) {
        return getSingleResult(getFindByFieldQuery("name", value, false));
    }

    public Entity findByField(String field, Object value) {
        return getSingleResult(getFindByFieldQuery(field, value, false));
    }

    public Entity findByFieldLowerCase(String field, Object value) {
        return getSingleResult(getFindByFieldQuery(field, value, true));
    }

    public List<Entity> findByFieldList(String field, Object value) {
        return getList(getFindByFieldQuery(field, value, false));
    }

    public Entity findByField(String field1, Object value1, String field2, Object value2) {
        return getSingleResult(getFindByFieldQuery(field1, value1, field2, value2));
    }

    public List<Entity> findByFieldList(String field1, String value1, String field2, String value2) {
        return getList(getFindByFieldQuery(field1, value1, field2, value2));
    }

    public Entity createOrUpdate(Entity entity) {
        Entity merged = getEntityManager().merge(entity);
        getEntityManager().persist(merged);
        return merged;
    }

    public void delete(Entity deletable) {
        if (!(deletable instanceof Deletable)) {
            throw new RuntimeException();
        }
        getEntityManager().createQuery(UPDATE + name() + ALIAS + SET_DELETED_TRUE +
                WHERE + fieldInEquals(ID)).executeUpdate();
    }

    private String getDistinctSelect() {
        return "select distinct o from " + name() + ALIAS;
    }

    private String getCountSelect() {
        return "select count(o) from " + name() + ALIAS;
    }

    private TypedQuery<Entity> getFindByFieldInQuery(String field, List<? extends Serializable> value) {
        return getEntityManager().createQuery(select() + WHERE + fieldInEquals(field), entity()).setParameter(field, value);
    }

    private TypedQuery<Entity> getFindByFieldQuery(String field, Object value, boolean useCase) {
        String fieldEquals = useCase ? fieldEqualsLower(field) : fieldEquals(field);
        return getEntityManager().createQuery(select() + WHERE + fieldEquals, entity())
                .setParameter(field, useCase ? value.toString().toLowerCase() : value);
    }

    private TypedQuery<Entity> getFindByFieldQuery(String field1, Object value1, String field2, Object value2) {
        return getEntityManager()
                .createQuery(select() + WHERE + fieldEquals(field1) + AND + fieldEquals(field2), entity())
                .setParameter(field1, value1)
                .setParameter(field2, value2);
    }

    private Entity getSingleResult(TypedQuery<Entity> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
//            logger.debug("Query had no results.");
            return null;
        }
    }

    private List<Entity> getList(TypedQuery<Entity> query) {
        return query.getResultList();
    }

    public String name() {
        return entity().getSimpleName();
    }

    public String select() {
        return "select o from " + name() + ALIAS;
    }

    private String fieldEquals(String field) {
        return "o." + field + " = :" + field;
    }

    private String fieldEqualsLower(String field) {
        return "lower(o." + field + ") = :" + field;
    }

    private String fieldInEquals(String field) {
        return "o." + field + " in (:" + field + ")";
    }
}

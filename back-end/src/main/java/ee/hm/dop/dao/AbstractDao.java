package ee.hm.dop.dao;

import com.google.common.collect.Lists;
import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.Deletable;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractDao<Entity extends AbstractEntity> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String ALIAS = " o ";
    public static final String WHERE = " where ";
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String ID = "id";
    public static final String SET_DELETED_TRUE = " set o.deleted = true ";
    public static final String UPDATE = "update ";
    public static final String DELETED = "deleted";
    public static final String NAME = "name";
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

    public Entity findByIdValid(Long id) {
        return findByField(ID, id, DELETED, false);
    }

    public List<Entity> findById(List<Long> id) {
        if (CollectionUtils.isEmpty(id)){
            return Lists.newArrayList();
        }
        return getList(getFindByFieldInQuery(ID, id));
    }

    public List<Entity> findAll() {
        return getList(getEntityManager().createQuery(select(), entity()));
    }

    public List<Entity> findAllValid() {
        return findByFieldList(DELETED, false);
    }

    private void isDeletable() {
        if (!Deletable.class.isAssignableFrom(entity)) {
            throw new RuntimeException(" Entity: " + entity + " does not implement Deletable");
        }
    }

    public List<Entity> findAllDeleted() {
        return findByFieldList(DELETED, true);
    }

    public Entity findByName(String value) {
        return getSingleResult(getFindByFieldQuery(NAME, value, false));
    }

    public List<Entity> findByName(List<String> value) {
        if (CollectionUtils.isEmpty(value)){
            return Lists.newArrayList();
        }
        return getList(getFindByFieldInQuery(NAME, value));
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

    public Entity findByField(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        return getSingleResult(getFindByFieldQuery(field1, value1, field2, value2, field3, value3));
    }

    public List<Entity> findByFieldList(String field1, Object value1, String field2, Object value2) {
        return getList(getFindByFieldQuery(field1, value1, field2, value2));
    }

    public List<Entity> findByFieldList(String field1, Object value1, String field2, Object value2, String field3, Object value3) {
        return getList(getFindByFieldQuery(field1, value1, field2, value2, field3, value3));
    }

    public Entity createOrUpdate(Entity entity) {
        Entity merged = getEntityManager().merge(entity);
        getEntityManager().persist(merged);
        return merged;
    }

    public void setDeleted(List<Long> id) {
        getEntityManager()
                .createQuery(UPDATE + name() + ALIAS + SET_DELETED_TRUE + WHERE + fieldInEquals(ID))
                .setParameter(ID, id)
                .executeUpdate();
    }

    public void remove(Entity entity) {
        getEntityManager().remove(entity);
    }

    public Object getCount(){
        return getEntityManager().createQuery(countSelect()).getSingleResult();
    }

    public Object getCountByField(String field, Object value){
        return getEntityManager()
                .createQuery(countSelect() + WHERE + fieldEquals(field))
                .setParameter(field, value)
                .getSingleResult();
    }

    private TypedQuery<Entity> getFindByFieldInQuery(String field, List<? extends Serializable> value) {
        return getEntityManager()
                .createQuery(select() + WHERE + fieldInEquals(field), entity())
                .setParameter(field, value);
    }

    private TypedQuery<Entity> getFindByFieldQuery(String field, Object value, boolean useCase) {
        String fieldEquals = useCase ? fieldEqualsLower(field) : fieldEquals(field);
        return getEntityManager()
                .createQuery(select() + WHERE + fieldEquals, entity())
                .setParameter(field, useCase ? value.toString().toLowerCase() : value);
    }

    private TypedQuery<Entity> getFindByFieldQuery(String field1, Object value1, String field2, Object value2) {
        return getEntityManager()
                .createQuery(select() + WHERE + fieldEquals(field1) + AND + fieldEquals(field2), entity())
                .setParameter(field1, value1)
                .setParameter(field2, value2);
    }

    private TypedQuery<Entity> getFindByFieldQuery(String field1, Object value1, String field2, Object value2, String field3, Object value3)  {
        return getEntityManager()
                .createQuery(select() + WHERE + fieldEquals(field1) + AND + fieldEquals(field2) + AND + fieldEquals(field3), entity())
                .setParameter(field1, value1)
                .setParameter(field2, value2)
                .setParameter(field3, value3);
    }

    public Entity getSingleResult(TypedQuery<? extends Entity> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.debug("Query had no results." + query.getParameters());
            return null;
        } catch (NonUniqueResultException e){
            logger.debug("Query had more than 1 results." + query.getParameters());
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

    private String countSelect() {
        return "select count(o) from " + name() + ALIAS;
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

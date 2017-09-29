package ee.hm.dop.utils;

import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;

import java.util.function.Function;

public class ValidatorUtil {

    public static final String PERMISSION_MSG = "Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.";
    public static final String NOT_FOUND = " not found";
    public static final String ALREADY_EXISTS = " already exists.";

    public static RuntimeException permissionError() {
        return new RuntimeException(PERMISSION_MSG);
    }

    public static Portfolio findValid(Portfolio entity, Function<Long, Portfolio> getFromDb){
        mustHaveId(entity);
        Portfolio dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity);
        return dbEntity;
    }

    public static Material findValid(Material entity, Function<Long, Material> getFromDb){
        mustHaveId(entity);
        Material dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity);
        return dbEntity;
    }

    public static LearningObject findValid(LearningObject entity, Function<Long, LearningObject> getFromDb){
        mustHaveId(entity);
        LearningObject dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity);
        return dbEntity;
    }

    public static void mustNotHaveId(Portfolio entity) {
        mustNotHaveId(entity, Portfolio.class);
    }

    public static void mustNotHaveId(Material entity) {
        mustNotHaveId(entity, Material.class);
    }

    public static void mustNotHaveId(LearningObject entity) {
        mustNotHaveId(entity, LearningObject.class);
    }

    public static void mustHaveEntity(Portfolio entity){
        mustHaveEntity(entity, Portfolio.class);
    }

    public static void mustHaveId(Portfolio entity){
        mustHaveId(entity, Portfolio.class);
    }

    public static void mustHaveEntity(Material entity){
        mustHaveEntity(entity, Material.class);
    }

    public static void mustHaveId(Material entity){
        mustHaveId(entity, Material.class);
    }

    public static void mustHaveEntity(LearningObject entity){
        mustHaveEntity(entity, LearningObject.class);
    }

    public static void mustHaveId(LearningObject entity){
        mustHaveId(entity, LearningObject.class);
    }

    private static void mustHaveEntity(AbstractEntity entity, Class<? extends AbstractEntity> clazz){
        if (entity == null){
            throw notFound(clazz);
        }
    }

    private static void mustHaveId(AbstractEntity entity, Class<? extends AbstractEntity> clazz){
        if (entity == null || entity.getId() == null){
            throw notFound(clazz);
        }
    }

    private static RuntimeException notFound(Class<? extends AbstractEntity> clazz) {
        return new RuntimeException(clazz.getSimpleName() + NOT_FOUND);
    }

    public static void mustNotHaveId(AbstractEntity entity, Class<? extends AbstractEntity> clazz) {
        if (entity.getId() != null) {
            throw alreadyExists(clazz);
        }
    }

    private static RuntimeException alreadyExists(Class<? extends AbstractEntity> clazz) {
        return new RuntimeException(clazz.getSimpleName() + ALREADY_EXISTS);
    }
}

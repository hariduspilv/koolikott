package ee.hm.dop.utils;

import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.interfaces.ILearningObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

public class ValidatorUtil {

    public static final String PERMISSION_MSG = "Object does not exist or requesting user must be logged in user must be the creator, administrator or moderator.";
    public static final String NOT_FOUND = " not found ";
    public static final String ALREADY_EXISTS = " already exists.";

    public static RuntimeException permissionError() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, PERMISSION_MSG);
    }

    public static Portfolio findValid(Portfolio entity, Function<Long, Portfolio> getFromDb){
        mustHaveId(entity, entity != null ? entity.getId() : null);
        Portfolio dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity, entity.getId());
        return dbEntity;
    }

    public static Material findValid(Material entity, Function<Long, Material> getFromDb){
        mustHaveId(entity, entity != null ? entity.getId() : null);
        Material dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity, entity.getId());
        return dbEntity;
    }

    public static LearningObject findValid(LearningObject entity, Function<Long, LearningObject> getFromDb){
        mustHaveId(entity, entity != null ? entity.getId() : null);
        LearningObject dbEntity = getFromDb.apply(entity.getId());
        mustHaveEntity(dbEntity, entity.getId());
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

    public static void mustHaveEntity(Portfolio entity, Long id){
        mustHaveEntity(entity, Portfolio.class, id);
    }

    public static void mustHaveId(Portfolio entity, Long id){
        mustHaveId(entity, Portfolio.class, id);
    }

    public static void mustHaveEntity(Material entity, Long id){
        mustHaveEntity(entity, Material.class, id);
    }

    public static void mustHaveId(Material entity, Long id){
        mustHaveId(entity, Material.class, id);
    }

    public static void mustHaveEntity(LearningObject entity, Long id){
        mustHaveEntity(entity, LearningObject.class, id);
    }

    public static void mustHaveId(ILearningObject entity, Long id){
        mustHaveId(entity, LearningObject.class, id);
    }

    private static void mustHaveEntity(AbstractEntity entity, Class<? extends AbstractEntity> clazz, Long id){
        if (entity == null){
            throw notFound(clazz, id);
        }
    }

    private static void mustHaveId(AbstractEntity entity, Class<? extends AbstractEntity> clazz, Long id){
        if (entity == null || entity.getId() == null){
            throw notFound(clazz, id);
        }
    }

    private static void mustNotHaveId(AbstractEntity entity, Class<? extends AbstractEntity> clazz) {
        if (entity.getId() != null) {
            throw alreadyExists(clazz);
        }
    }

    private static RuntimeException notFound(Class<? extends AbstractEntity> clazz, Long id) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, clazz.getSimpleName() + NOT_FOUND + id);
    }

    private static RuntimeException alreadyExists(Class<? extends AbstractEntity> clazz) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, clazz.getSimpleName() + ALREADY_EXISTS);
    }
}

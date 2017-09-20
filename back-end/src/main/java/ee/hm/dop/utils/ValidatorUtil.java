package ee.hm.dop.utils;

import ee.hm.dop.model.AbstractEntity;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;

import java.util.function.Function;

public class ValidatorUtil {

    public static void findValid(Portfolio entity, Function<Long, Portfolio> getFromDb){
        validateId(entity, Portfolio.class);
        Portfolio dbEntity = getFromDb.apply(entity.getId());
        validateEntity(dbEntity, Portfolio.class);
    }

    public static void findValid(Material entity, Function<Long, Material> getFromDb){
        validateId(entity, Material.class);
        Material dbEntity = getFromDb.apply(entity.getId());
        validateEntity(dbEntity, Material.class);
    }

    public static void findValid(LearningObject entity, Function<Long, LearningObject> getFromDb){
        validateId(entity, Material.class);
        LearningObject dbEntity = getFromDb.apply(entity.getId());
        validateEntity(dbEntity, Material.class);
    }

    public static void validateEntity(Portfolio entity){
        validateEntity(entity, Portfolio.class);
    }

    public static void validateId(Portfolio entity){
        validateId(entity, Portfolio.class);
    }

    public static void validateEntity(Material entity){
        validateEntity(entity, Material.class);
    }

    public static void validateId(Material entity){
        validateId(entity, Material.class);
    }

    public static void validateEntity(LearningObject entity){
        validateEntity(entity, LearningObject.class);
    }

    public static void validateId(LearningObject entity){
        validateId(entity, LearningObject.class);
    }

    private static void validateEntity(AbstractEntity entity, Class<? extends AbstractEntity> clazz){
        if (entity == null){
            throw new RuntimeException(clazz.getSimpleName() + " not found");
        }
    }

    private static void validateId(AbstractEntity entity, Class<? extends AbstractEntity> clazz){
        if (entity == null || entity.getId() == null){
            throw new RuntimeException(clazz.getSimpleName() + " not found");
        }
    }
}

package ee.hm.dop.service.metadata;


import ee.hm.dop.dao.TargetGroupDao;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.enums.TargetGroupEnum;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class TargetGroupService {

    private static final String TARGET_GROUP_TRANSLATION_PREFIX = "TARGET_GROUP_";
    @Inject
    private TranslationService translationService;
    @Inject
    private TargetGroupDao targetGroupDao;

    public List<TargetGroup> getValues() {
        return targetGroupDao.findAll();
    }

    public TargetGroup getByName(String name) {
        return targetGroupDao.findByName(name);
    }

    public List<TargetGroup> getByName(List<String> name) {
        return targetGroupDao.findByName(name);
    }

    public TargetGroup getByTranslation(String translation) {
        String translationKey = translationService.getTranslationKeyByTranslation(translation);
        if (translationKey == null) {
            return null;
        }

        try {
            TargetGroupEnum targetGroupEnum = TargetGroupEnum.valueOf(translationKey.replaceFirst("^" + TARGET_GROUP_TRANSLATION_PREFIX, ""));
            return targetGroupDao.findByName(targetGroupEnum.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public List<TargetGroup> getTargetGroupsByAge(int from, int to) {
        List<String> enumNames = TargetGroupEnum.getTargetGroupEnumNames(from, to);
        return getByName(enumNames);
    }
}

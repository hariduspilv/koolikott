package ee.hm.dop.service;


import com.google.inject.Inject;
import ee.hm.dop.dao.TargetGroupDAO;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.TargetGroupEnum;
import org.apache.commons.lang3.Range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TargetGroupService {

    private static final String TARGET_GROUP_TRANSLATION_PREFIX = "TARGET_GROUP_";
    @Inject
    private TranslationService translationService;

    @Inject
    private TargetGroupDAO targetGroupDAO;

    public List<TargetGroup> getValues() {
        return targetGroupDAO.getAll();
    }

    public TargetGroup getByName(String name) {
        return targetGroupDAO.getByName(name);
    }

    TargetGroup getByTranslation(String translation) {
        String translationKey = translationService.getTranslationKeyByTranslation(translation);
        if (translationKey == null) {
            return null;
        }

        try {
            TargetGroupEnum targetGroupEnum = TargetGroupEnum.valueOf(translationKey.replaceFirst("^" + TARGET_GROUP_TRANSLATION_PREFIX, ""));
            return targetGroupDAO.getByName(targetGroupEnum.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Set<TargetGroup> getTargetGroupsByAge(int from, int to) {
        Set<TargetGroup> targetGroups = new HashSet<>();
        Range<Integer> range = Range.between(from, to);

        for (TargetGroupEnum targetGroupEnum : TargetGroupEnum.values()) {
            if (targetGroupEnum.getRange().isOverlappedBy(range)) {
                targetGroups.add(getByName(targetGroupEnum.name()));
            }
        }

        return targetGroups;
    }
}

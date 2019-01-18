package ee.hm.dop.service.permission;

import java.util.HashMap;
import java.util.Map;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionFactory {

    private MaterialPermission materialPermission;
    private PortfolioPermission portfolioPermission;

    private static Map<Class<? extends LearningObject>, PermissionItem> map = new HashMap<>();

    {
        register(Material.class, materialPermission);
        register(Portfolio.class, portfolioPermission);
    }

    private static void register(Class<? extends LearningObject> learningObjectClass, PermissionItem learningObjectHandlerClass) {
        map.put(learningObjectClass, learningObjectHandlerClass);
    }

    public PermissionItem get(Class<? extends LearningObject> clazz) {
        return map.get(clazz);
    }
}
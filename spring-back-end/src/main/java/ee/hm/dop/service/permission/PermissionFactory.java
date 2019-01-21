package ee.hm.dop.service.permission;

import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.Material;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
@Transactional
public class PermissionFactory {

    @Inject
    private MaterialPermission materialPermission;
    @Inject
    private PortfolioPermission portfolioPermission;

    public PermissionItem get(LearningObject clazz) {
        if (clazz instanceof Material) {
            return materialPermission;
        }
        return portfolioPermission;
    }
}
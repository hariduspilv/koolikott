package ee.hm.dop.rest;

import ee.hm.dop.model.UserTourData;
import ee.hm.dop.service.useractions.UserTourDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("userTourData")
public class UserTourDataResource extends BaseResource {

    @Inject
    private UserTourDataService userTourDataService;

    @GetMapping
    public UserTourData getUserTourData() {
        return userTourDataService.getUserTourData(getLoggedInUser());
    }

    @PutMapping
    public UserTourData addUserTourData(@RequestBody UserTourData userTourData) {
        return userTourDataService.addUserTourData(userTourData, getLoggedInUser());
    }
}

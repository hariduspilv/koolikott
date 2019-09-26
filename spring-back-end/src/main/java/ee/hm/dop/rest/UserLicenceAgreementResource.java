package ee.hm.dop.rest;

import ee.hm.dop.dao.UserDao;
import ee.hm.dop.model.UserLicenceAgreement;
import ee.hm.dop.service.UserLicenceAgreementService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("userLicenceAgreement")
public class UserLicenceAgreementResource extends BaseResource {

    @Inject
    UserLicenceAgreementService userLicenceAgreementService;
    @Inject
    UserDao userDao;

    @GetMapping
    public UserLicenceAgreement get(@RequestParam(value = "id") Long id) {
        return userLicenceAgreementService.getLatestUserLicenceAgreement(id);
    }

    @PostMapping
    public UserLicenceAgreement post(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);

        return userLicenceAgreementService.setUserLicenceAgreement(
                userDao.findUserById(jsonObject.getLong("userId")),
                jsonObject.getBoolean("agreed"),
                jsonObject.getBoolean("disagreed"));
    }

}

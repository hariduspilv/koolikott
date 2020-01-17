package ee.hm.dop.service.login.dto;

import ee.hm.dop.model.enums.LoginFrom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAgreementDto {

    private boolean existingUser;
    private boolean userConfirmed;
    private boolean statusOk;
    private String token;
    private Long userTermsAgreement;
    private Long gdprTermsAgreement;
    private LoginFrom loginFrom;

}

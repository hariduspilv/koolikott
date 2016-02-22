package ee.hm.dop.guice.provider;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;

import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SignatureValidatorTestProvider implements Provider<SignatureValidator> {

    private SignatureValidator validator;

    @Override
    public SignatureValidator get() {
        return initValidator();
    }

    private SignatureValidator initValidator() {
        validator = createNiceMock(SignatureValidator.class);

        try {
            validator.validate(EasyMock.anyObject(Signature.class));
        } catch (ValidationException e) {

        }

        replay(validator);

        return validator;
    }
}

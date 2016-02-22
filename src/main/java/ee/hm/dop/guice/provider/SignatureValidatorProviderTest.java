package ee.hm.dop.guice.provider;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.signature.SignatureValidator;

import ee.hm.dop.common.test.GuiceTestRunner;
import ee.hm.dop.guice.GuiceInjector;

@RunWith(GuiceTestRunner.class)
public class SignatureValidatorProviderTest {

    private SignatureValidatorProvider signatureValidatorProvider;

    @Before
    public void setup() {
        signatureValidatorProvider = GuiceInjector.getInjector().getInstance(SignatureValidatorProvider.class);
    }

    @BeforeClass
    public static void init() throws ConfigurationException {
        DefaultBootstrap.bootstrap();
    }

    @Test
    public void get() {
        SignatureValidator validator = signatureValidatorProvider.get();
        assertNotNull(validator);
    }

}

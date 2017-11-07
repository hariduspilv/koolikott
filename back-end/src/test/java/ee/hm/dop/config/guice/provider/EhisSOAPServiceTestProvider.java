package ee.hm.dop.config.guice.provider;

import ee.hm.dop.model.ehis.Person;
import ee.hm.dop.service.ehis.IEhisSOAPService;

public class EhisSOAPServiceTestProvider extends EhisSOAPServiceProvider {

    private IEhisSOAPService instance = new EhisSOAPServiceMock();

    @Override
    public IEhisSOAPService get() {
        return instance;
    }

}
class EhisSOAPServiceMock implements IEhisSOAPService {

    @Override
    public Person getPersonInformation(String idCode) {
        return null;
    }
}

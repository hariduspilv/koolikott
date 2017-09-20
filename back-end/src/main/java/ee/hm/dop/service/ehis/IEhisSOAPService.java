package ee.hm.dop.service.ehis;

import ee.hm.dop.model.ehis.Person;

public interface IEhisSOAPService {

    Person getPersonInformation(String idCode);
}

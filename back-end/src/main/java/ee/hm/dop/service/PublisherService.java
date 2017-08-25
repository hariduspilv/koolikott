package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PublisherDao;
import ee.hm.dop.model.Publisher;

/**
 * Created by mart on 2.12.15.
 */
public class PublisherService {

    @Inject
    private PublisherDao publisherDao;

    public Publisher getPublisherByName(String name) {
        return publisherDao.findByName(name);
    }

    public Publisher createPublisher(String name, String website) {
        Publisher publisher = new Publisher();
        publisher.setName(name);
        publisher.setWebsite(website);
        return publisherDao.createOrUpdate(publisher);
    }
}

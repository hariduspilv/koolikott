package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.PublisherDAO;
import ee.hm.dop.model.Publisher;

/**
 * Created by mart on 2.12.15.
 */
public class PublisherService {

    @Inject
    private PublisherDAO publisherDAO;

    public Publisher getPublisherByName(String name) {
        return publisherDAO.findPublisherByName(name);
    }

    public Publisher createPublisher(String name, String website) {
        Publisher publisher = new Publisher();
        publisher.setName(name);
        publisher.setWebsite(website);
        return publisherDAO.create(publisher);
    }
}

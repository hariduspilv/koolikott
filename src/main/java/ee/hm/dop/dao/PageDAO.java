package ee.hm.dop.dao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import ee.hm.dop.model.Language;
import ee.hm.dop.model.Page;

public class PageDAO {

    @Inject
    private EntityManager entityManager;

    public Page findByNameAndLang(String name, Language language) {

        TypedQuery<Page> findByNameAndLang = entityManager.createNamedQuery("Page.findByNameAndLang", Page.class);

        Page page = null;
        try {
            page = findByNameAndLang.setParameter("pageName", name).setParameter("language", language)
                    .getSingleResult();
        } catch (NoResultException ex) {
            // ignore
        }

        return page;

    }

}

package ee.hm.dop.service;

import static java.lang.String.format;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.model.Author;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Publisher;
import ee.hm.dop.model.User;

public class MaterialService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialDAO materialDao;

    @Inject
    private AuthorService authorService;

    @Inject
    private PublisherService publisherService;

    public Material get(long materialId) {
        return materialDao.findById(materialId);
    }

    public List<Material> getNewestMaterials(int numberOfMaterials) {
        return materialDao.findNewestMaterials(numberOfMaterials);
    }

    public void increaseViewCount(Material material) {
        material.setViews(material.getViews() + 1);
        createOrUpdate(material);
    }

    public Material createMaterial(Material material, User creator) {
        if (material.getId() != null) {
            throw new IllegalArgumentException("Error creating Material, material already exists.");
        }
        material.setCreator(creator);
        setAuthors(material);
        setPublishers(material);

        return createOrUpdate(material);
    }

    private void setPublishers(Material material) {
        List<Publisher> publishers = material.getPublishers();

        if (publishers != null) {
            for (int i = 0; i < publishers.size(); i++) {
                Publisher publisher = publishers.get(i);
                if (publisher != null) {
                    Publisher returnedPublisher = publisherService.getPublisherByName(publisher.getName());
                    if (returnedPublisher != null) {
                        publishers.set(i, returnedPublisher);
                    } else {
                        returnedPublisher = publisherService.createPublisher(publisher.getName(), publisher.getWebsite());
                        publishers.set(i, returnedPublisher);
                    }
                }
            }
        }
    }

    private void setAuthors(Material material) {
        List<Author> authors = material.getAuthors();
        if (authors != null) {
            for (int i = 0; i < authors.size(); i++) {
                Author author = authors.get(i);
                if (author != null) {
                    Author returnedAuthor = authorService.getAuthorByFullName(author.getName(), author.getSurname());
                    if (returnedAuthor != null) {
                        authors.set(i, returnedAuthor);
                    } else {
                        returnedAuthor = authorService.createAuthor(author.getName(), author.getSurname());
                        authors.set(i, returnedAuthor);
                    }
                }
            }
        }
    }

    public void update(Material material) {
        Material originalMaterial = materialDao.findById(material.getId());
        validateMaterialUpdate(material, originalMaterial);

        // Should not be able to update view count
        material.setViews(originalMaterial.getViews());
        // Should not be able to update added date, must keep the original
        material.setAdded(originalMaterial.getAdded());

        createOrUpdate(material);
    }

    private void validateMaterialUpdate(Material material, Material originalMaterial) {
        if (originalMaterial == null) {
            throw new IllegalArgumentException("Error updating Material: material does not exist.");
        }

        final String ErrorModifyRepository = "Error updating Material: Not allowed to modify repository.";
        if (material.getRepository() == null && originalMaterial.getRepository() != null) {
            throw new IllegalArgumentException(ErrorModifyRepository);
        }

        if (material.getRepository() != null && !material.getRepository().equals(originalMaterial.getRepository())) {
            throw new IllegalArgumentException(ErrorModifyRepository);
        }
    }

    public byte[] getMaterialPicture(Material material) {
        return materialDao.findPictureByMaterial(material);
    }

    public List<Material> getByCreator(User creator) {
        return materialDao.findByCreator(creator);
    }

    private Material createOrUpdate(Material material) {
        Long materialId = material.getId();
        if (materialId != null) {
            logger.info(format("Updating material %s", materialId));
        } else {
            logger.info("Creating material.");
        }

        return materialDao.update(material);
    }

    public void delete(Material material) {
        materialDao.delete(material);
    }
}

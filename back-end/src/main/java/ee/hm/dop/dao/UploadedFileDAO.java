package ee.hm.dop.dao;

import javax.persistence.TypedQuery;

import ee.hm.dop.model.UploadedFile;

public class UploadedFileDAO extends BaseDAO<UploadedFile> {
    public UploadedFile findUploadedFileById(Long id) {
        TypedQuery<UploadedFile> findById = createQuery("FROM UploadedFile uf WHERE uf.id = :id", UploadedFile.class)
                .setParameter("id", id);
        return getSingleResult(findById);
    }
}

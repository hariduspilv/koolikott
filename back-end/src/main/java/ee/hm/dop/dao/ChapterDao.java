package ee.hm.dop.dao;

import ee.hm.dop.model.Chapter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ChapterDao extends AbstractDao<Chapter> {

    public List<Chapter> chaptersWithPortfolio() {
        if (0==0){
            return new ArrayList<>();
        }
        return getEntityManager()
                .createNativeQuery("SELECT * FROM Chapter c " +
                        "WHERE c.Portfolio IS NOT NULL " +
                        "AND c.parentChapter IS NULL ", Chapter.class)
                .getResultList();
    }

    public void deleteMaterialRow(List<Long> rowId) {
        if (CollectionUtils.isNotEmpty(rowId)) {
            getEntityManager()
                    .createNativeQuery("DELETE FROM Row_Material WHERE row = :id")
                    .setParameter("id", rowId)
                    .executeUpdate();
        }
    }

    public void deleteContentRow(List<Long> rowId) {
        if (CollectionUtils.isNotEmpty(rowId)) {
            getEntityManager()
                    .createNativeQuery("DELETE FROM ContentRow WHERE id = :id")
                    .setParameter("id", rowId)
                    .executeUpdate();
        }
    }

    public void deleteChapterRow(List<Long> rowId) {
        if (CollectionUtils.isNotEmpty(rowId)) {
            getEntityManager()
                    .createNativeQuery("DELETE FROM Chapter_Row WHERE row = :id")
                    .setParameter("id", rowId)
                    .executeUpdate();
        }
    }

    public void deleteChapter(List<Long> chapterId) {
        if (CollectionUtils.isNotEmpty(chapterId)) {
            getEntityManager()
                    .createNativeQuery("DELETE FROM Chapter WHERE id = :id")
                    .setParameter("id", chapterId)
                    .executeUpdate();
        }
    }

    public void updateChapterText(List<Long> chapterId) {
        if (CollectionUtils.isNotEmpty(chapterId)) {
            getEntityManager()
                    .createNativeQuery("UPDATE Chapter set textValue = NULL WHERE id = :id")
                    .setParameter("id", chapterId)
                    .executeUpdate();
        }
    }
}

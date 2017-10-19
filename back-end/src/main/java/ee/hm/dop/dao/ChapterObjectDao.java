package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;

import ee.hm.dop.model.ChapterObject;
import ee.hm.dop.model.LearningObject;
import ee.hm.dop.model.enums.Visibility;

public class ChapterObjectDao extends AbstractDao<ChapterObject> {

    public ChapterObject update(ChapterObject chapterObject) {
        chapterObject.setLastInteraction(now());
        chapterObject.setUpdated(now());
        chapterObject.setAdded(now());
<<<<<<< HEAD:back-end/src/main/java/ee/hm/dop/dao/ChapterObjectDao.java
=======
        chapterObject.setVisibility(Visibility.PUBLIC);
>>>>>>> new-develop:back-end/src/main/java/ee/hm/dop/dao/ChapterObjectDao.java
        return createOrUpdate(chapterObject);
    }
}

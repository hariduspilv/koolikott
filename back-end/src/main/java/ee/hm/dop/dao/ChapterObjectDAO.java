package ee.hm.dop.dao;

import static org.joda.time.DateTime.now;

import ee.hm.dop.model.ChapterObject;
import ee.hm.dop.model.LearningObject;

/**
 * Created by mart on 13.12.16.
 */
public class ChapterObjectDAO extends BaseDAO<ChapterObject> {

    @Override
    public ChapterObject update(ChapterObject chapterObject) {
        chapterObject.setLastInteraction(now());
        chapterObject.setUpdated(now());
        chapterObject.setAdded(now());

        return super.update(chapterObject);
    }
}

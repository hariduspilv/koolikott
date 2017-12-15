package ee.hm.dop.service.content;

import ee.hm.dop.dao.MediaDao;
import ee.hm.dop.model.Media;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.UrlUtil;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class MediaService {

    @Inject
    private MediaDao mediaDao;

    public Media save(Media media, User loggedInUser) {
        if (media.getId() != null){
            throw new UnsupportedOperationException("saving requires null id");
        }
        if (media.getUrl() == null){
            throw new UnsupportedOperationException("must have url");
        }
        media.setSource(UrlUtil.processURL(media.getSource()));
        media.setCreatedBy(loggedInUser);
        media.setCreatedAt(DateTime.now());
        return mediaDao.createOrUpdate(media);
    }

    public Media update(Media media, User loggedInUser) {
        if (media.getId() == null){
            throw new UnsupportedOperationException("update must have id");
        }
        if (media.getUrl() == null){
            throw new UnsupportedOperationException("must have url");
        }
        Media dbMedia = mediaDao.findById(media.getId());
        media.setSource(UrlUtil.processURL(media.getSource()));
        media.setCreatedAt(dbMedia.getCreatedAt());
        media.setCreatedBy(dbMedia.getCreatedBy());
        return mediaDao.createOrUpdate(media);
    }

    public Media get(long mediaId, User loggedInUser) {
        return mediaDao.findById(mediaId);
    }
}

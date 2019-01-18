package ee.hm.dop.service.content;

import ee.hm.dop.dao.MediaDao;
import ee.hm.dop.model.Media;
import ee.hm.dop.model.User;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
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
        media.setUrl(UrlUtil.processURL(media.getUrl()));
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
        if (!(UserUtil.isAdminOrModerator(loggedInUser) || UserUtil.isCreator(media, loggedInUser))){
            throw new UnsupportedOperationException(" must be admin, moderator or creator");
        }
        media.setUrl(UrlUtil.processURL(media.getUrl()));
        media.setCreatedAt(dbMedia.getCreatedAt());
        media.setCreatedBy(dbMedia.getCreatedBy());
        return mediaDao.createOrUpdate(media);
    }

    public Media get(long mediaId) {
        return mediaDao.findById(mediaId);
    }
}

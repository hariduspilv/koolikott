package ee.hm.dop.service.content;

import ee.hm.dop.dao.MediaDao;
import ee.hm.dop.model.*;
import ee.hm.dop.utils.UrlUtil;
import ee.hm.dop.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class MediaService {

    @Inject
    private MediaDao mediaDao;
    @Inject
    private PortfolioService portfolioService;

    public Media save(Media media, User loggedInUser) {
        if (media.getId() != null) {
            throw new UnsupportedOperationException("saving requires null id");
        }
        if (media.getUrl() == null) {
            throw new UnsupportedOperationException("must have url");
        }
        media.setUrl(UrlUtil.processURL(media.getUrl()));
        media.setCreatedBy(loggedInUser);
        media.setCreatedAt(LocalDateTime.now());
        return mediaDao.createOrUpdate(media);
    }

    public Media update(Media media, User loggedInUser) {
        if (media.getId() == null) {
            throw new UnsupportedOperationException("update must have id");
        }
        if (media.getUrl() == null) {
            throw new UnsupportedOperationException("must have url");
        }
        Media dbMedia = mediaDao.findById(media.getId());
        if (!(UserUtil.isAdminOrModerator(loggedInUser) || UserUtil.isCreator(media, loggedInUser))) {
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

    public List<Media> getAllByCreator(User creator) {
        return mediaDao.findAllByCreator(creator);
    }

    public List<Media> getAllMediaIfLearningObjectIsPortfolio(LearningObject lo) {
        List<Media> portfolioMedia = new ArrayList<>();
        Portfolio portfolio = portfolioService.findById(lo.getId());

        if (portfolio != null) {
            List<ChapterBlock> chapterBlocks = new ArrayList<>();
            portfolio.getChapters().forEach(chapter -> chapterBlocks.addAll(chapter.getBlocks()));
            chapterBlocks.forEach(chapterBlock -> portfolioMedia.addAll(getMediaFromChapterBlock(chapterBlock.getHtmlContent())));
        }

        return portfolioMedia;
    }

    private List<Media> getMediaFromChapterBlock(String htmlContent) {
        List<Media> media = new ArrayList<>();
        List<String> partsOfChapterBlock = Arrays.asList(htmlContent.split("<div"));

        partsOfChapterBlock.forEach(partOfChapterBlock -> {
            if (partOfChapterBlock.contains("media") && partOfChapterBlock.contains("data-id")) {
                Long id = parseMediaId(partOfChapterBlock);
                if (id > 0) {
                    media.add(mediaDao.findById(id));
                }
            }
        });

        return media;
    }

    private long parseMediaId(String partOfChapterBlock) {
        StringBuilder idString = new StringBuilder();
        String unparsedId = partOfChapterBlock.split("data-id")[1];

        unparsedId.chars().forEach(c -> {
            if (Character.isDigit((char) c)) {
                idString.append((char) c);
            }
        });

        return Long.parseLong(idString.toString());
    }
}

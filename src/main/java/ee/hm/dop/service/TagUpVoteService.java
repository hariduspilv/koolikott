package ee.hm.dop.service;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.TagDAO;
import ee.hm.dop.dao.TagUpVoteDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.User;

/**
 * Created by mart on 29.01.16.
 */
public class TagUpVoteService {

    @Inject
    private MaterialDAO materialDAO;

    @Inject
    private PortfolioDAO portfolioDAO;

    @Inject
    private TagDAO tagDAO;

    @Inject
    private TagUpVoteDAO tagUpVoteDAO;

    public TagUpVote upVote(TagUpVote tagUpVote, User user) {
        tagUpVote = createTagUpVote(tagUpVote, user);

        if(tagUpVote.getUser() != null && tagUpVote.getTag() != null && (tagUpVote.getMaterial() != null || tagUpVote.getPortfolio() != null) ) {
           return tagUpVoteDAO.update(tagUpVote);
        }  else {
            throw new RuntimeException("No material or portfolio or tag or user found when upvoting tag");
        }
    }

    private TagUpVote createTagUpVote(TagUpVote tagUpVote, User user) {
        Material material;
        Portfolio portfolio;
        if (tagUpVote.getMaterial() != null) {
            material = materialDAO.findByIdNotDeleted(tagUpVote.getMaterial().getId());
            tagUpVote.setMaterial(material);
        } else if (tagUpVote.getPortfolio() != null) {
            portfolio = portfolioDAO.findByIdNotDeleted(tagUpVote.getPortfolio().getId());
            tagUpVote.setPortfolio(portfolio);
        }

        tagUpVote.setUser(user);
        Tag tag = tagDAO.findTagByName(tagUpVote.getTag().getName());
        tagUpVote.setTag(tag);
        return tagUpVote;
    }

    public void removeUpVoteFromMaterial(Long tagID, Long materialID, User loggedInUser) {
        Material material = materialDAO.findByIdNotDeleted(materialID);
        Tag tag = tagDAO.findTagByID(tagID);

        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, material);

        tagUpVoteDAO.setDeleted(tagUpVote);
    }

    public void removeUpVoteFromPortfolio(Long tagID, Long portfolioID, User loggedInUser) {
        Portfolio portfolio = portfolioDAO.findByIdNotDeleted(portfolioID);
        Tag tag = tagDAO.findTagByID(tagID);

        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, portfolio);

        tagUpVoteDAO.setDeleted(tagUpVote);
    }
}

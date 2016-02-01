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
        Material material = null;
        Portfolio portfolio = null;

        if (tagUpVote.getMaterial() != null) {
            material = materialDAO.findByIdNotDeleted(tagUpVote.getMaterial().getId());
        } else if (tagUpVote.getPortfolio() != null) {
            portfolio = portfolioDAO.findByIdNotDeleted(tagUpVote.getPortfolio().getId());
        }

        tagUpVote.setUser(user);
        Tag tag = tagDAO.findTagByName(tagUpVote.getTag().getName());
        tagUpVote.setTag(tag);

        if(user != null && tag != null && (material != null || portfolio != null) ) {
           return tagUpVoteDAO.update(tagUpVote);
        }  else {
            throw new RuntimeException("No material or portfolio or tag or user found when upvoting tag");
        }
    }
}

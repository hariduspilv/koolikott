package ee.hm.dop.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ee.hm.dop.dao.MaterialDAO;
import ee.hm.dop.dao.PortfolioDAO;
import ee.hm.dop.dao.TagDAO;
import ee.hm.dop.dao.TagUpVoteDAO;
import ee.hm.dop.model.Material;
import ee.hm.dop.model.Portfolio;
import ee.hm.dop.model.Tag;
import ee.hm.dop.model.TagUpVote;
import ee.hm.dop.model.TagUpVoteForm;
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

    @Inject
    private SearchEngineService searchEngineService;

    public TagUpVote upVote(TagUpVote tagUpVote, User user) {
        tagUpVote = createTagUpVote(tagUpVote, user);

        if (tagUpVoteDAO.getTagUpVote(tagUpVote.getTag(), user, tagUpVote.getMaterial()) == null
                && tagUpVoteDAO.getTagUpVote(tagUpVote.getTag(), user, tagUpVote.getPortfolio()) == null) {
            if (tagUpVote.getUser() != null && tagUpVote.getTag() != null && (tagUpVote.getMaterial() != null || tagUpVote.getPortfolio() != null)) {
                TagUpVote returntagUpVote = tagUpVoteDAO.update(tagUpVote);
                searchEngineService.updateIndex();

                return returntagUpVote;
            } else {
                throw new RuntimeException("No material or portfolio or tag or user found when upvoting tag");
            }
        } else {
            throw new RuntimeException("Only one upVote allowed");

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

    public void removeUpVoteFromMaterial(Tag tag, Material material, User loggedInUser) {
        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, material);

        tagUpVoteDAO.setDeleted(tagUpVote);
        searchEngineService.updateIndex();
    }

    public void removeUpVoteFromPortfolio(Tag tag, Portfolio portfolio, User loggedInUser) {
        TagUpVote tagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, portfolio);

        tagUpVoteDAO.setDeleted(tagUpVote);
        searchEngineService.updateIndex();
    }

    public List<TagUpVoteForm> getMaterialTagUpVotes(Material material, User loggedInUser) {
        List<TagUpVoteForm> tagUpVoteForms = new ArrayList<>();
        if(material != null) {
            for(Tag tag : material.getTags()) {
                List<TagUpVote> materialTagUpVotes = tagUpVoteDAO.getMaterialTagUpVotes(material, tag);
                TagUpVote userTagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, material);
                boolean hasUserUpVoted = userTagUpVote != null;
                int count = materialTagUpVotes != null ? materialTagUpVotes.size() : 0;

                TagUpVoteForm tagUpVoteForm = new TagUpVoteForm(tag, count, hasUserUpVoted);
                tagUpVoteForms.add(tagUpVoteForm);
            }
        }

        return tagUpVoteForms;
    }

    public List<TagUpVoteForm> getPortfolioTagUpVotes(Portfolio portfolio, User loggedInUser) {
        List<TagUpVoteForm> tagUpVoteForms = new ArrayList<>();
        if(portfolio != null) {
            for(Tag tag : portfolio.getTags()) {
                List<TagUpVote> portfolioTagUpVotes = tagUpVoteDAO.getPortfolioTagUpVotes(portfolio, tag);
                TagUpVote userTagUpVote = tagUpVoteDAO.getTagUpVote(tag, loggedInUser, portfolio);
                boolean hasUserUpVoted = userTagUpVote != null;
                int count = portfolioTagUpVotes != null ? portfolioTagUpVotes.size() : 0;

                TagUpVoteForm tagUpVoteForm = new TagUpVoteForm(tag, count, hasUserUpVoted);
                tagUpVoteForms.add(tagUpVoteForm);
            }
        }

        return tagUpVoteForms;
    }
}

package ee.hm.dop.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.rest.jackson.map.DateTimeDeserializer;
import ee.hm.dop.rest.jackson.map.DateTimeSerializer;
import ee.hm.dop.rest.jackson.map.PictureDeserializer;

@Entity
public class Portfolio implements Searchable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime created;

	@Column
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private DateTime updated;

	@ManyToOne
	@JoinColumn(name = "taxon")
	private Taxon taxon;

	@ManyToOne
	@JoinColumn(name = "creator", nullable = false)
	private User creator;

	@Column(columnDefinition = "TEXT")
	private String summary;

	@Column(nullable = false)
	private Long views = (long) 0;

	@OneToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
	@JoinColumn(name = "portfolio")
	@OrderColumn(name = "chapterOrder")
	private List<Chapter> chapters;

	@OneToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
	@JoinColumn(name = "portfolio")
	@OrderBy("added DESC")
	private List<Comment> comments;

	@JsonIgnore
	@OneToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
	@JoinColumn(name = "portfolio")
	@OrderBy("added DESC")
	private List<UserLike> userLikes;

	@Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.portfolio = id AND ul.isLiked = 1)")
	private int likes;

	@Formula(value = "(SELECT COUNT(*) FROM UserLike ul WHERE ul.portfolio = id AND ul.isLiked = 0)")
	private int dislikes;

	@ManyToMany(fetch = EAGER, cascade = { MERGE, PERSIST })
	@JoinTable(name = "Portfolio_Tag", joinColumns = { @JoinColumn(name = "portfolio") }, inverseJoinColumns = {
			@JoinColumn(name = "tag") }, uniqueConstraints = @UniqueConstraint(columnNames = { "portfolio", "tag" }) )
	private List<Tag> tags;

	@Lob
	private byte[] picture;

	@Formula("picture is not null")
	private boolean hasPicture;

	@Enumerated(EnumType.STRING)
	@Column(name = "targetGroup")
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "Portfolio_TargetGroup", joinColumns = @JoinColumn(name = "portfolio") )
	private List<TargetGroup> targetGroups;

	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "Portfolio_CrossCurricularTheme", joinColumns = {
			@JoinColumn(name = "portfolio") }, inverseJoinColumns = {
					@JoinColumn(name = "crossCurricularTheme") }, uniqueConstraints = @UniqueConstraint(columnNames = {
							"portfolio", "crossCurricularTheme" }) )
	private List<CrossCurricularTheme> crossCurricularThemes;

	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "Portfolio_KeyCompetence", joinColumns = {
			@JoinColumn(name = "portfolio") }, inverseJoinColumns = {
					@JoinColumn(name = "keyCompetence") }, uniqueConstraints = @UniqueConstraint(columnNames = {
							"portfolio", "keyCompetence" }) )
	private List<KeyCompetence> keyCompetences;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Visibility visibility;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@JsonSerialize(using = DateTimeSerializer.class)
	public DateTime getCreated() {
		return created;
	}

	@JsonDeserialize(using = DateTimeDeserializer.class)
	public void setCreated(DateTime created) {
		this.created = created;
	}

	@JsonSerialize(using = DateTimeSerializer.class)
	public DateTime getUpdated() {
		return updated;
	}

	@JsonDeserialize(using = DateTimeDeserializer.class)
	public void setUpdated(DateTime updated) {
		this.updated = updated;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Long getViews() {
		return views;
	}

	public void setViews(Long views) {
		this.views = views;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@JsonIgnore
	public byte[] getPicture() {
		return picture;
	}

	@JsonProperty
	@JsonDeserialize(using = PictureDeserializer.class)
	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public boolean getHasPicture() {
		return hasPicture;
	}

	public void setHasPicture(boolean hasPicture) {
		this.hasPicture = hasPicture;
	}

	public List<TargetGroup> getTargetGroups() {
		return targetGroups;
	}

	public void setTargetGroups(List<TargetGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<CrossCurricularTheme> getCrossCurricularThemes() {
		return crossCurricularThemes;
	}

	public void setCrossCurricularThemes(List<CrossCurricularTheme> crossCurricularThemes) {
		this.crossCurricularThemes = crossCurricularThemes;
	}

	public List<KeyCompetence> getKeyCompetences() {
		return keyCompetences;
	}

	public void setKeyCompetences(List<KeyCompetence> keyCompetences) {
		this.keyCompetences = keyCompetences;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public List<UserLike> getUserLikes() {
		return userLikes;
	}

	public void setUserLikes(List<UserLike> userLikes) {
		this.userLikes = userLikes;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDislikes() {
		return dislikes;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

}

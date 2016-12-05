package ee.hm.dop.service;

import static ee.hm.dop.service.SolrService.getTokenizedQueryString;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.ImmutableSet;
import ee.hm.dop.dao.LearningObjectDAO;
import ee.hm.dop.dao.UserFavoriteDAO;
import ee.hm.dop.model.CrossCurricularTheme;
import ee.hm.dop.model.KeyCompetence;
import ee.hm.dop.model.Language;
import ee.hm.dop.model.ResourceType;
import ee.hm.dop.model.Role;
import ee.hm.dop.model.SearchFilter;
import ee.hm.dop.model.SearchResult;
import ee.hm.dop.model.Searchable;
import ee.hm.dop.model.TargetGroup;
import ee.hm.dop.model.User;
import ee.hm.dop.model.UserFavorite;
import ee.hm.dop.model.Visibility;
import ee.hm.dop.model.solr.Document;
import ee.hm.dop.model.solr.Response;
import ee.hm.dop.model.solr.SearchResponse;
import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.Topic;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

public class SearchService {

    private static final String MATERIAL_TYPE = "material";

    private static final String PORTFOLIO_TYPE = "portfolio";

    private static final String ALL_TYPE = "all";

    @Inject
    private SolrEngineService solrEngineService;

    @Inject
    private LearningObjectDAO learningObjectDAO;

    @Inject
    private UserFavoriteDAO userFavoriteDAO;

    public SearchResult search(String query, long start, Long limit, SearchFilter searchFilter) {
        SearchResult searchResult = new SearchResult();

        searchFilter.setVisibility(getSearchVisibility(searchFilter.getRequestingUser()));
        Long resultCount;
        if (limit != null && limit == 0) resultCount = null;
        else resultCount = limit;

        SearchResponse searchResponse = doSearch(query, start, resultCount, searchFilter);
        Response response = searchResponse.getResponse();

        if (response != null) {
            List<Document> documents = response.getDocuments();
            List<Searchable> unsortedSearchable = retrieveSearchedItems(documents, searchFilter.getRequestingUser());
            List<Searchable> sortedSearchable = sortSearchable(documents, unsortedSearchable);

            searchResult.setStart(response.getStart());
            // "- documents.size() + sortedSearchable.size()" needed in case
            // SearchEngine and DB are not sync because of re-indexing time.
            if (limit == null || limit > 0) {
                searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());
                searchResult.setItems(sortedSearchable);
            } else if (limit == 0) {
                //When limit is 0 - only getting metainfo
                searchResult.setTotalResults(response.getTotalResults() - documents.size() + sortedSearchable.size());
            }
        }

        return searchResult;
    }

    private List<Visibility> getSearchVisibility(User loggedInUser) {
        List<Visibility> visibilities = new ArrayList<Visibility>() {{
            add(Visibility.PUBLIC);
        }};

        if (loggedInUser != null) {
            if (loggedInUser.getRole() == Role.ADMIN) {
                // Add private and not listed, so admin can see all searchables
                visibilities.add(Visibility.NOT_LISTED);
                visibilities.add(Visibility.PRIVATE);
            } else if (loggedInUser.getRole() == Role.MODERATOR) {
                visibilities.add(Visibility.NOT_LISTED);
            }
        }

        return visibilities;
    }

    private List<Searchable> retrieveSearchedItems(List<Document> documents, User loggedInUser) {
        List<Long> learningObjectIds = new ArrayList<>();
        for (Document document : documents) {
            learningObjectIds.add(document.getId());
        }

        List<Searchable> unsortedSearchable = new ArrayList<>();

        if (!learningObjectIds.isEmpty()) {
            learningObjectDAO.findAllById(learningObjectIds).forEach(searchable -> {
                if(loggedInUser != null) {
                    UserFavorite userFavorite = userFavoriteDAO.findFavoriteByUserAndLearningObject(searchable.getId(), loggedInUser);
                    if (userFavorite != null && userFavorite.getId() != null) searchable.setFavorite(true);
                    else searchable.setFavorite(false);
                }

                unsortedSearchable.add(searchable);
            });
        }

        return unsortedSearchable;
    }

    private SearchResponse doSearch(String query, long start, Long limit, SearchFilter searchFilter) {
        String queryString = getTokenizedQueryString(query);

        String filtersAsQuery = getFiltersAsQuery(searchFilter);
        if (!filtersAsQuery.isEmpty()) {
            if (!queryString.isEmpty()) {
                queryString = format("(%s) AND %s", queryString, filtersAsQuery);
            } else {
                queryString = filtersAsQuery;
            }
        }

        if (queryString.isEmpty()) {
            throw new RuntimeException("No query string and filters present.");
        }

        if (limit == null) {
            return solrEngineService.search(queryString, start, getSort(searchFilter));
        }

        return solrEngineService.search(queryString, start, limit, getSort(searchFilter));
    }

    private String getSort(SearchFilter searchFilter) {
        String sort = null;
        if (searchFilter.getSort() != null && searchFilter.getSortDirection() != null) {
            sort = String.join(" ", searchFilter.getSort(), searchFilter.getSortDirection().getValue());
        }
        return sort;
    }

    private List<Searchable> sortSearchable(List<Document> indexList, List<Searchable> unsortedSearchable) {
        Map<Long, Searchable> idToSearchable = new HashMap<>();

        for (Searchable searchable : unsortedSearchable)
            idToSearchable.put(searchable.getId(), searchable);

        return indexList.stream()
                .filter(document -> idToSearchable.containsKey(document.getId()))
                .map(document -> idToSearchable.get(document.getId()))
                .collect(Collectors.toList());
    }

    /*
     * Convert filters to Solr syntax query
     */
    private String getFiltersAsQuery(SearchFilter searchFilter) {
        List<String> filters = new LinkedList<>();

        filters.add(getLanguageAsQuery(searchFilter));
        filters.add(getTaxonsAsQuery(searchFilter));
        filters.add(isPaidAsQuery(searchFilter));
        filters.add(getTypeAsQuery(searchFilter));
        filters.add(getTargetGroupsAsQuery(searchFilter));
        filters.add(getResourceTypeAsQuery(searchFilter));
        filters.add(isSpecialEducationAsQuery(searchFilter));
        filters.add(issuedFromAsQuery(searchFilter));
        filters.add(getCrossCurricularThemeAsQuery(searchFilter));
        filters.add(getKeyCompetenceAsQuery(searchFilter));
        filters.add(isCurriculumLiteratureAsQuery(searchFilter));
        filters.add(getVisibilityAsQuery(searchFilter));
        filters.add(getCreatorAsQuery(searchFilter));

        // Remove empty elements
        filters = filters.stream().filter(f -> !f.isEmpty()).collect(Collectors.toList());

        return StringUtils.join(filters, " AND ");
    }

    private String getLanguageAsQuery(SearchFilter searchFilter) {
        Language language = searchFilter.getLanguage();
        if (language != null) {
            return format("(language:\"%s\" OR type:\"portfolio\")", language.getCode());
        }
        return "";
    }

    private String isPaidAsQuery(SearchFilter searchFilter) {
        if (!searchFilter.isPaid()) {
            return "(paid:\"false\" OR type:\"portfolio\")";
        }
        return "";
    }

    private String getTypeAsQuery(SearchFilter searchFilter) {
        Set<String> types = ImmutableSet.of(MATERIAL_TYPE, PORTFOLIO_TYPE, ALL_TYPE);

        String type = searchFilter.getType();
        if (type != null) {
            type = ClientUtils.escapeQueryChars(type).toLowerCase();
            if (types.contains(type)) {
                if (type.equals("all")) {
                    return "(type:\"material\" OR type:\"portfolio\")";
                }

                return format("type:\"%s\"", type);
            }
        }

        return "";
    }

    private String getTargetGroupsAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getTargetGroups() != null && !searchFilter.getTargetGroups().isEmpty()) {
            List<TargetGroup> targetGroups = searchFilter.getTargetGroups();
            List<String> filters = new ArrayList<>();

            for (TargetGroup targetGroup : targetGroups) {
                filters.add(format("target_group:\"%s\"", targetGroup.toString().toLowerCase()));
            }

            if (filters.size() == 1) {
                return filters.get(0);
            }

            return "(" + StringUtils.join(filters, " OR ") + ")";
        }

        return "";
    }

    private String getTaxonsAsQuery(SearchFilter searchFilter) {
        List<Taxon> taxonList = searchFilter.getTaxons();
        List<String> taxons = new LinkedList<>();
        List<String> joinedTaxons = new ArrayList<>();

        if (taxonList == null) {
            return "";
        }

        for (Taxon taxon : taxonList) {
            if (taxon instanceof Subtopic) {
                addTaxonToQuery(taxon, taxons);
                taxon = ((Subtopic) taxon).getTopic();
            }

            if (taxon instanceof Topic) {
                addTaxonToQuery(taxon, taxons);

                Subject subject = ((Topic) taxon).getSubject();
                Domain domain = ((Topic) taxon).getDomain();
                Module module = ((Topic) taxon).getModule();

                if (subject != null) {
                    taxon = subject;
                } else if (domain != null) {
                    taxon = domain;
                } else if (module != null) {
                    taxon = module;
                }
            }

            if (taxon instanceof Subject) {
                addTaxonToQuery(taxon, taxons);
                taxon = ((Subject) taxon).getDomain();
            }

            if (taxon instanceof Module) {
                addTaxonToQuery(taxon, taxons);
                taxon = ((Module) taxon).getSpecialization();
            }

            if (taxon instanceof Specialization) {
                addTaxonToQuery(taxon, taxons);
                taxon = ((Specialization) taxon).getDomain();
            }

            if (taxon instanceof Domain) {
                addTaxonToQuery(taxon, taxons);
                taxon = ((Domain) taxon).getEducationalContext();
            }

            if (taxon instanceof EducationalContext) {
                addTaxonToQuery(taxon, taxons);
            }

            if (taxonList.size() == 1) {
                return StringUtils.join(taxons, " AND ");
            }

            joinedTaxons.add("(" + StringUtils.join(taxons, " AND ") + ")");
            taxons.clear();
        }

        return !joinedTaxons.isEmpty() ? "(" + StringUtils.join(joinedTaxons, " OR ") + ")" : "";
    }

    private void addTaxonToQuery(Taxon taxon, List<String> taxons) {
        String name = getTaxonName(taxon);
        String taxonLevel = getTaxonLevel(taxon);
        if (taxonLevel != null) {
            taxons.add(format("%s:\"%s\"", taxonLevel, name));
        }
    }

    private String getTaxonName(Taxon taxon) {
        return ClientUtils.escapeQueryChars(taxon.getName()).toLowerCase();
    }

    private String getTaxonLevel(Taxon taxon) {
        if (taxon instanceof EducationalContext) {
            return "educational_context";
        } else if (taxon instanceof Domain) {
            return "domain";
        } else if (taxon instanceof Subject) {
            return "subject";
        } else if (taxon instanceof Topic) {
            return "topic";
        } else if (taxon instanceof Subtopic) {
            return "subtopic";
        } else if (taxon instanceof Specialization) {
            return "specialization";
        } else if (taxon instanceof Module) {
            return "module";
        }
        return null;
    }

    private String getResourceTypeAsQuery(SearchFilter searchFilter) {
        ResourceType resourceType = searchFilter.getResourceType();
        if (resourceType != null) {
            return format("resource_type:\"%s\"", resourceType.getName().toLowerCase());
        }
        return "";
    }

    private String isSpecialEducationAsQuery(SearchFilter searchFilter) {
        if (searchFilter.isSpecialEducation()) {
            return "special_education:\"true\"";
        }
        return "";
    }

    private String issuedFromAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getIssuedFrom() != null) {
            return format("(issue_date_year:[%s TO *] OR (added:[%s-01-01T00:00:00Z TO *] AND type:\"portfolio\"))",
                    searchFilter.getIssuedFrom(), searchFilter.getIssuedFrom());
        }
        return "";
    }

    private String getCrossCurricularThemeAsQuery(SearchFilter searchFilter) {
        CrossCurricularTheme crossCurricularTheme = searchFilter.getCrossCurricularTheme();
        if (crossCurricularTheme != null) {
            return format("cross_curricular_theme:\"%s\"", crossCurricularTheme.getName().toLowerCase());
        }
        return "";
    }

    private String getKeyCompetenceAsQuery(SearchFilter searchFilter) {
        KeyCompetence keyCompetence = searchFilter.getKeyCompetence();
        if (keyCompetence != null) {
            return format("key_competence:\"%s\"", keyCompetence.getName().toLowerCase());
        }
        return "";
    }

    private String isCurriculumLiteratureAsQuery(SearchFilter searchFilter) {
        Boolean isCurriculumLiterature = searchFilter.isCurriculumLiterature();
        if (Boolean.TRUE.equals(isCurriculumLiterature)) {
            return "peerReview:[* TO *]";
        }

        return "";
    }

    private String getVisibilityAsQuery(SearchFilter searchFilter) {
        List<Visibility> visibilities = searchFilter.getVisibility();
        List<String> filter = visibilities
                .stream()
                .map(visibility -> format("visibility:\"%s\"", visibility.toString().toLowerCase()))
                .collect(Collectors.toList());

        //Visible to user according to their role or is a material or is the creator
        String query = "((" + StringUtils.join(filter, " OR ") + ") OR type:\"material\")";
        if (searchFilter.getRequestingUser() != null && searchFilter.getMyPrivates()) {
            query = query + " OR creator:" + searchFilter.getRequestingUser().getId();
        }

        return query;
    }

    private String getCreatorAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getCreator() != null) {
            return "creator:" + searchFilter.getCreator();
        }

        return "";
    }
}

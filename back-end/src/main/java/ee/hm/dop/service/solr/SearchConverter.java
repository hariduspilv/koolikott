package ee.hm.dop.service.solr;

import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.*;
import ee.hm.dop.utils.tokenizer.DOPSearchStringTokenizer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class SearchConverter {

    static String composeQueryString(String query, SearchFilter searchFilter) {
        String tokenizedQueryString = getTokenizedQueryString(query);
        String filtersAsQuery = getFiltersAsQuery(searchFilter);

        String queryString = getQueryString(searchFilter, tokenizedQueryString, filtersAsQuery);
        if (queryString.isEmpty()) throw new RuntimeException("No query string and filters present.");
        return queryString;
    }

    private static String getTokenizedQueryString(String query) {
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(query)) {
            query = query.replaceAll("\\+", " ");
            DOPSearchStringTokenizer tokenizer = new DOPSearchStringTokenizer(query);
            while (tokenizer.hasMoreTokens()) {
                sb.append(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static String getQueryString(SearchFilter searchFilter, String tokenizedQueryString, String filtersAsQuery) {
        if (StringUtils.isEmpty(filtersAsQuery)) return SearchService.EMPTY;
        if (StringUtils.isEmpty(tokenizedQueryString)) return filtersAsQuery;

        String queryString = format("((%s)", tokenizedQueryString);

        //Search for full phrase also, as they are more relevant
        if (!searchFilter.isFieldSpecificSearch())
            queryString = queryString.concat(format(" OR (\"%s\")", tokenizedQueryString));

        return queryString.concat(format(") %s %s", searchFilter.getSearchType(), filtersAsQuery));
    }

    /**
     * Convert filters to Solr syntax query
     */
    public static String getFiltersAsQuery(SearchFilter searchFilter) {
        List<String> filters = new LinkedList<>();

        filters.add(getLanguageAsQuery(searchFilter));
        filters.add(getTaxonsAsQuery(searchFilter));
        filters.add(isPaidAsQuery(searchFilter));
        filters.add(getTypeAsQuery(searchFilter));
        filters.add(getTargetGroupsAsQuery(searchFilter));
        filters.add(getResourceTypeAsQuery(searchFilter));
        filters.add(isSpecialEducationAsQuery(searchFilter));
        filters.add(issuedFromAsQuery(searchFilter));
        filters.add(getCrossCurricularThemesAsQuery(searchFilter));
        filters.add(getKeyCompetencesAsQuery(searchFilter));
        filters.add(isCurriculumLiteratureAsQuery(searchFilter));
        filters.add(getRecommendedAndFavoritesAsQuery(searchFilter));
        filters.add(getVisibilityAsQuery(searchFilter));
        filters.add(getCreatorAsQuery(searchFilter));

        // Remove empty elements
        String delimiter = " " + searchFilter.getSearchType() + " ";
        String filter = filters.stream().filter(f -> !f.isEmpty()).collect(Collectors.joining(delimiter));
        return filter.concat(getExcludedAsQuery(searchFilter));
    }

    public static List<Visibility> getSearchVisibility(User loggedInUser) {
        List<Visibility> visibilities = new ArrayList<>();
        visibilities.add(Visibility.PUBLIC);
        if (loggedInUser != null) {
            if (loggedInUser.getRole() == Role.ADMIN) {
                visibilities.add(Visibility.NOT_LISTED);
                visibilities.add(Visibility.PRIVATE);
            } else if (loggedInUser.getRole() == Role.MODERATOR) {
                visibilities.add(Visibility.NOT_LISTED);
            }
        }
        return visibilities;
    }

    private static String getExcludedAsQuery(SearchFilter searchFilter) {
        List<Long> excluded = searchFilter.getExcluded();
        if (CollectionUtils.isEmpty(excluded)) {
            return SearchService.EMPTY;
        }
        List<String> result = excluded.stream().map(id -> "-id:" + id.toString()).collect(Collectors.toList());
        return SearchService.AND + StringUtils.join(result, SearchService.AND);
    }

    private static String getLanguageAsQuery(SearchFilter searchFilter) {
        Language language = searchFilter.getLanguage();
        if (language != null) {
            return format("(language:\"%s\" OR type:\"portfolio\")", language.getCode());
        }
        return SearchService.EMPTY;
    }

    private static String isPaidAsQuery(SearchFilter searchFilter) {
        if (!searchFilter.isPaid()) {
            return "(paid:\"false\" OR type:\"portfolio\")";
        }
        return SearchService.EMPTY;
    }

    private static String getVisibilityAsQuery(SearchFilter searchFilter) {
        String filter = searchFilter.getVisibility().stream()
                .map(visibility -> format("visibility:\"%s\"", visibility.toString().toLowerCase()))
                .collect(Collectors.joining(SearchService.OR));

        //Visible to user according to their role or is a material or is the creator
        String query = "(" + filter + ")";
        if (searchFilter.getRequestingUser() != null && searchFilter.getMyPrivates()) {
            query = query + " OR creator:" + searchFilter.getRequestingUser().getId();
        }
        return query;
    }

    private static String getCreatorAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getCreator() != null) {
            return "creator:" + searchFilter.getCreator();
        }
        return SearchService.EMPTY;
    }

    private static String getTypeAsQuery(SearchFilter searchFilter) {
        String type = searchFilter.getType();
        if (type != null) {
            type = ClientUtils.escapeQueryChars(type).toLowerCase();
            if (SearchService.SEARCH_TYPES.contains(type)) {
                if (type.equals(SearchService.ALL_TYPE)) {
                    return "(type:\"material\" OR type:\"portfolio\")";
                }
                return format("type:\"%s\"", type);
            }
        }
        return SearchService.EMPTY;
    }

    private static String getTargetGroupsAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getTargetGroups() != null && !searchFilter.getTargetGroups().isEmpty()) {
            List<String> filters = new ArrayList<>();

            for (TargetGroup targetGroup : searchFilter.getTargetGroups()) {
                if (targetGroup != null) filters.add(format("target_group:\"%s\"", targetGroup.getId()));
            }
            if (filters.size() == 1) return filters.get(0);
            return filters.stream().collect(Collectors.joining(SearchService.OR, "(", ")"));
        }
        return SearchService.EMPTY;
    }

    private static void addTaxonToQuery(Taxon taxon, List<String> taxons) {
        String name = getTaxonName(taxon);
        if (taxon.getSolrLevel() != null) {
            taxons.add(format("%s:\"%s\"", taxon.getSolrLevel(), name));
        }
    }

    private static String getTaxonName(Taxon taxon) {
        return ClientUtils.escapeQueryChars(taxon.getName()).toLowerCase();
    }

    private static String getResourceTypeAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getResourceType() != null) {
            return format("resource_type:\"%s\"", searchFilter.getResourceType().getName().toLowerCase());
        }
        return SearchService.EMPTY;
    }

    private static String isSpecialEducationAsQuery(SearchFilter searchFilter) {
        if (searchFilter.isSpecialEducation()) {
            return "special_education:\"true\"";
        }
        return SearchService.EMPTY;
    }

    private static String getRecommendedAndFavoritesAsQuery(SearchFilter searchFilter) {
        if (!searchFilter.isRecommended() && !searchFilter.isFavorites()) {
            return SearchService.EMPTY;
        }
        if (searchFilter.getRequestingUser() == null) {
            if (searchFilter.isRecommended()) {
                return "recommended:\"true\"";
            }
            return SearchService.EMPTY;
        }
        if (searchFilter.isRecommended() && searchFilter.isFavorites()) {
            return "(recommended:\"true\" OR favored_by_user:\"" + searchFilter.getRequestingUser().getUsername() + "\")";
        }
        if (searchFilter.isRecommended()) {
            return "recommended:\"true\"";
        }
        if (searchFilter.isFavorites()) {
            return "favored_by_user:\"" + searchFilter.getRequestingUser().getUsername() + "\"";
        }
        return SearchService.EMPTY;
    }

    private static String issuedFromAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getIssuedFrom() != null) {
            return format("(issue_date_year:[%s TO *] OR (added:[%s-01-01T00:00:00Z TO *] AND type:\"portfolio\"))",
                    searchFilter.getIssuedFrom(), searchFilter.getIssuedFrom());
        }
        return SearchService.EMPTY;
    }

    private static String getCrossCurricularThemesAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getCrossCurricularThemes() != null && !searchFilter.getCrossCurricularThemes().isEmpty()) {
            List<String> themes = new ArrayList<>();

            for (CrossCurricularTheme crossCurricularTheme : searchFilter.getCrossCurricularThemes()) {
                themes.add(format("cross_curricular_theme:\"%s\"", crossCurricularTheme.getName().toLowerCase()));
            }
            if (themes.size() == 1) return themes.get(0);
            return themes.stream().collect(Collectors.joining(SearchService.OR, "(", ")"));
        }
        return SearchService.EMPTY;
    }

    private static String getKeyCompetencesAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getKeyCompetences() != null && !searchFilter.getKeyCompetences().isEmpty()) {
            List<String> competences = new ArrayList<>();

            for (KeyCompetence keyCompetence : searchFilter.getKeyCompetences()) {
                competences.add(format("key_competence:\"%s\"", keyCompetence.getName().toLowerCase()));
            }

            if (competences.size() == 1) return competences.get(0);
            return competences.stream().collect(Collectors.joining(SearchService.OR, "(", ")"));
        }
        return SearchService.EMPTY;
    }

    private static String isCurriculumLiteratureAsQuery(SearchFilter searchFilter) {
        if (searchFilter.isCurriculumLiterature()) {
            return "(peerReview:[* TO *] OR curriculum_literature:\"true\")";
        }
        return SearchService.EMPTY;
    }

    private static String getTaxonsAsQuery(SearchFilter searchFilter) {
        List<Taxon> taxonList = searchFilter.getTaxons();
        List<String> taxons = new LinkedList<>();
        List<String> joinedTaxons = new ArrayList<>();

        if (taxonList == null) return SearchService.EMPTY;

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

                if (subject != null) taxon = subject;
                else if (domain != null) taxon = domain;
                else if (module != null) taxon = module;
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
                return StringUtils.join(taxons, SearchService.AND);
            }

            joinedTaxons.add(taxons.stream().collect(Collectors.joining(SearchService.AND, "(", ")")));
            taxons.clear();
        }

        return joinedTaxons.isEmpty() ? SearchService.EMPTY
                : joinedTaxons.stream().collect(Collectors.joining(SearchService.OR, "(", ")"));
    }

}

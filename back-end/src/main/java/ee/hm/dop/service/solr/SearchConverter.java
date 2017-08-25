package ee.hm.dop.service.solr;

import com.google.common.collect.ImmutableSet;
import ee.hm.dop.model.*;
import ee.hm.dop.model.enums.Role;
import ee.hm.dop.model.enums.Visibility;
import ee.hm.dop.model.taxon.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.util.ClientUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class SearchConverter {
    public static List<Visibility> getSearchVisibility(User loggedInUser) {
        List<Visibility> visibilities = new ArrayList<>();
        visibilities.add(Visibility.PUBLIC);
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

    public static String getExcludedAsQuery(SearchFilter searchFilter) {
        List<Long> excluded = searchFilter.getExcluded();
        if (CollectionUtils.isNotEmpty(excluded)) {
            List<String> result = excluded.stream().map(id -> "-id:" + id.toString()).collect(Collectors.toList());
            return SearchService.AND + StringUtils.join(result, SearchService.AND);
        }

        return "";
    }

    public static String getLanguageAsQuery(SearchFilter searchFilter) {
        Language language = searchFilter.getLanguage();
        if (language != null) {
            return format("(language:\"%s\" OR type:\"portfolio\")", language.getCode());
        }
        return "";
    }

    public static String isPaidAsQuery(SearchFilter searchFilter) {
        if (!searchFilter.isPaid()) {
            return "(paid:\"false\" OR type:\"portfolio\")";
        }
        return "";
    }

    public static  String getVisibilityAsQuery(SearchFilter searchFilter) {
        List<Visibility> visibilities = searchFilter.getVisibility();
        List<String> filter = visibilities
                .stream()
                .map(visibility -> format("visibility:\"%s\"", visibility.toString().toLowerCase()))
                .collect(Collectors.toList());

        //Visible to user according to their role or is a material or is the creator
        String query = "((" + StringUtils.join(filter, SearchService.OR) + ") OR type:\"material\")";
        if (searchFilter.getRequestingUser() != null && searchFilter.getMyPrivates()) {
            query = query + " OR creator:" + searchFilter.getRequestingUser().getId();
        }

        return query;
    }

    public static  String getCreatorAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getCreator() != null) {
            return "creator:" + searchFilter.getCreator();
        }

        return SearchService.EMPTY;
    }

    public static String getTypeAsQuery(SearchFilter searchFilter) {
        Set<String> types = ImmutableSet.of(SearchService.MATERIAL_TYPE, SearchService.PORTFOLIO_TYPE, SearchService.ALL_TYPE);

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
        return SearchService.EMPTY;
    }

    public static String getTargetGroupsAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getTargetGroups() != null && !searchFilter.getTargetGroups().isEmpty()) {
            List<String> filters = new ArrayList<>();

            for (TargetGroup targetGroup : searchFilter.getTargetGroups()) {
                if (targetGroup != null) {
                    filters.add(format("target_group:\"%s\"", targetGroup.getId()));
                }
            }
            if (filters.size() == 1) {
                return filters.get(0);
            }
            return "(" + StringUtils.join(filters, SearchService.OR) + ")";
        }
        return SearchService.EMPTY;
    }

    public static  void addTaxonToQuery(Taxon taxon, List<String> taxons) {
        String name = getTaxonName(taxon);
        String taxonLevel = getTaxonLevel(taxon);
        if (taxonLevel != null) {
            taxons.add(format("%s:\"%s\"", taxonLevel, name));
        }
    }

    public static  String getTaxonName(Taxon taxon) {
        return ClientUtils.escapeQueryChars(taxon.getName()).toLowerCase();
    }

    public static  String getTaxonLevel(Taxon taxon) {
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

    public static  String getResourceTypeAsQuery(SearchFilter searchFilter) {
        ResourceType resourceType = searchFilter.getResourceType();
        if (resourceType != null) {
            return format("resource_type:\"%s\"", resourceType.getName().toLowerCase());
        }
        return SearchService.EMPTY;
    }

    public static  String isSpecialEducationAsQuery(SearchFilter searchFilter) {
        if (searchFilter.isSpecialEducation()) {
            return "special_education:\"true\"";
        }
        return SearchService.EMPTY;
    }

    public static  String issuedFromAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getIssuedFrom() != null) {
            return format("(issue_date_year:[%s TO *] OR (added:[%s-01-01T00:00:00Z TO *] AND type:\"portfolio\"))",
                    searchFilter.getIssuedFrom(), searchFilter.getIssuedFrom());
        }
        return SearchService.EMPTY;
    }

    public static  String getCrossCurricularThemesAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getCrossCurricularThemes() != null && !searchFilter.getCrossCurricularThemes().isEmpty()) {
            List<String> themes = new ArrayList<>();

            for (CrossCurricularTheme crossCurricularTheme : searchFilter.getCrossCurricularThemes()) {
                themes.add(format("cross_curricular_theme:\"%s\"", crossCurricularTheme.getName().toLowerCase()));
            }

            if (themes.size() == 1) {
                return themes.get(0);
            }

            return "(" + StringUtils.join(themes, SearchService.OR) + ")";
        }

        return SearchService.EMPTY;
    }

    public static  String getKeyCompetencesAsQuery(SearchFilter searchFilter) {
        if (searchFilter.getKeyCompetences() != null && !searchFilter.getKeyCompetences().isEmpty()) {
            List<String> competences = new ArrayList<>();

            for (KeyCompetence keyCompetence : searchFilter.getKeyCompetences()) {
                competences.add(format("key_competence:\"%s\"", keyCompetence.getName().toLowerCase()));
            }

            if (competences.size() == 1) {
                return competences.get(0);
            }

            return "(" + StringUtils.join(competences, SearchService.OR) + ")";
        }

        return SearchService.EMPTY;
    }

    public static  String isCurriculumLiteratureAsQuery(SearchFilter searchFilter) {
        Boolean isCurriculumLiterature = searchFilter.isCurriculumLiterature();
        if (Boolean.TRUE.equals(isCurriculumLiterature)) {
            return "(peerReview:[* TO *] OR curriculum_literature:\"true\")";
        }

        return SearchService.EMPTY;
    }

    public static String getTaxonsAsQuery(SearchFilter searchFilter) {
        List<Taxon> taxonList = searchFilter.getTaxons();
        List<String> taxons = new LinkedList<>();
        List<String> joinedTaxons = new ArrayList<>();

        if (taxonList == null) {
            return SearchService.EMPTY;
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
                return StringUtils.join(taxons, SearchService.AND);
            }

            joinedTaxons.add("(" + StringUtils.join(taxons, SearchService.AND) + ")");
            taxons.clear();
        }

        return joinedTaxons.isEmpty() ? SearchService.EMPTY : "(" + StringUtils.join(joinedTaxons, SearchService.OR) + ")";
    }
}

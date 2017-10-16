package ee.hm.dop.utils;

import ee.hm.dop.model.taxon.*;

import java.util.List;

import static ee.hm.dop.model.taxon.TaxonLevel.*;
import static java.util.Arrays.asList;

/**
 * Valid full taxonomy trees are:
 * Educational context -> Domain -> Subject -> Topic -> Subtopic
 * Educational context -> Domain -> Topic -> Subtopic
 * Educational context -> Domain -> Specialization -> Module -> Topic -> Subtopic
 * <p>
 * You can have shorter trees, for that you remove elements from the right.
 * Tree always starts with Educational Context
 */
public class TaxonUtils {

    /**
     * Educational context is top parent
     */
    public static EducationalContext getEducationalContext(Taxon taxon) {
        return (EducationalContext) getParent(EducationalContext.class, taxon);
    }

    public static Domain getDomain(Taxon taxon) {
        return (Domain) getParent(Domain.class, taxon);
    }

    public static Subject getSubject(Taxon taxon) {
        return (Subject) getParent(Subject.class, taxon);
    }

    public static Specialization getSpecialization(Taxon taxon) {
        return (Specialization) getParent(Specialization.class, taxon);
    }

    public static Module getModule(Taxon taxon) {
        return (Module) getParent(Module.class, taxon);
    }

    public static Topic getTopic(Taxon taxon) {
        return (Topic) getParent(Topic.class, taxon);
    }

    public static Subtopic getSubtopic(Taxon taxon) {
        return (Subtopic) getParent(Subtopic.class, taxon);
    }

    public static List<String> getLevelTree(Taxon taxon) {
        if (taxon instanceof EducationalContext) {
            return asList(EDUCATIONAL_CONTEXT, DOMAIN, SUBJECT, SPECIALIZATION, MODULE, TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Domain) {
            return asList(DOMAIN, SUBJECT, SPECIALIZATION, MODULE, TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Subject) {
            return asList(SUBJECT, TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Specialization) {
            return asList(SPECIALIZATION, MODULE, TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Module) {
            return asList(MODULE, TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Topic) {
            return asList(TOPIC, SUBTOPIC);
        }
        if (taxon instanceof Subtopic) {
            return asList(SUBTOPIC);
        }
        throw new UnsupportedOperationException("Unknown taxon: " + taxon);
    }

    /**
     * returns parent when level is reached
     * returns itself if it is at the required level
     */
    public static Taxon getParent(Class<? extends Taxon> parentClazz, Taxon taxon) {
        if (taxon == null) {
            return null;
        }
        if (parentClazz.isInstance(taxon)) {
            return taxon;
        }
        return getParent(parentClazz, taxon.getParent());
    }

    /**
     * returns parent when level is reached and ids match parent and child ids match
     * returns itself if it is at the required level
     */
    public static Taxon getParent(Taxon parent, Taxon child) {
        if (child == null) {
            return null;
        }
        if (parent.getClass().isInstance(child) && parent.getId().equals(child.getId())) {
            return child;
        }
        return getParent(parent, child.getParent());
    }
}

package ee.hm.dop.utils;

import ee.hm.dop.model.taxon.Domain;
import ee.hm.dop.model.taxon.EducationalContext;
import ee.hm.dop.model.taxon.Module;
import ee.hm.dop.model.taxon.Specialization;
import ee.hm.dop.model.taxon.Subject;
import ee.hm.dop.model.taxon.Subtopic;
import ee.hm.dop.model.taxon.Taxon;
import ee.hm.dop.model.taxon.Topic;

/**
 * Valid full taxonomy trees are:
 * Educational context -> Domain -> Subject -> Topic -> Subtopic
 * Educational context -> Domain -> Topic -> Subtopic
 * Educational context -> Domain -> Specialization -> Module -> Topic -> Subtopic
 *
 * You can have shorter trees, for that you remove elements from the right.
 * Tree always starts with Educational Context
 */
public class TaxonUtils {

    /**
     *  Educational context is top parent
     */
    public static EducationalContext getEducationalContext(Taxon taxon) {
        EducationalContext educationalContext = null;

        if (taxon instanceof Subtopic) {
            taxon = ((Subtopic) taxon).getTopic();
        }

        if (taxon instanceof Topic) {
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
            taxon = ((Subject) taxon).getDomain();
        }

        if (taxon instanceof Module) {
            taxon = ((Module) taxon).getSpecialization();
        }

        if (taxon instanceof Specialization) {
            taxon = ((Specialization) taxon).getDomain();
        }

        if (taxon instanceof Domain) {
            taxon = ((Domain) taxon).getEducationalContext();
        }

        if (taxon instanceof EducationalContext) {
            educationalContext = (EducationalContext) taxon;
        }

        return educationalContext;
    }

}

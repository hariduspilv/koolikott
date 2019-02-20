package ee.hm.dop.dao.firstreview;

public class FirstReviewQueryHelper {

    public static final String userTaxonQuery1 =  "Select TP1.taxon \n" +
            " from User_Taxon ut,TaxonPosition TP1\n" +
            " where ut.user = :userTaxonId \n" +
            " and (ut.taxon = TP1.educationalContext\n" +
            "or ut.taxon = TP1.domain\n" +
            "or ut.taxon = TP1.subject\n" +
            "or ut.taxon = TP1.module\n" +
            "or ut.taxon = TP1.specialization\n" +
            "or ut.taxon = TP1.topic\n" +
            "or ut.taxon = TP1.subtopic)\n" +
            "group by taxon";
}

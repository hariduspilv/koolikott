package ee.hm.dop.dao;

import ee.hm.dop.model.LanguageString;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LanguageStringDao extends AbstractDao<LanguageString> {

    public List<LanguageString> findAll() {
        return getEntityManager()
                .createNativeQuery("" +
                        " select P.title as lang\n" +
                        " from LearningObject LO\n" +
                        " join Portfolio P on LO.id = P.id\n" +
                        " union all\n" +
                        " select LS.textValue as lang\n" +
                        " from LearningObject LO\n" +
                        " join Material M on LO.id = M.id\n" +
                        " join Material_Title MT on M.id = MT.material\n" +
                        " join LanguageString LS on MT.title = LS.id")
                .getResultList();
    }

    public List<String> findAllPortfolioTitles() {
        return getEntityManager().createNativeQuery("select P.title as lang\n" +
                " from LearningObject LO\n" +
                " join Portfolio P on LO.id = P.id")
                .getResultList();
    }

    public List<String> findAllMaterialTitles() {
        return getEntityManager().createNativeQuery("select LS.textValue as lang\n" +
                " from LearningObject LO\n" +
                " join Material M on LO.id = M.id\n" +
                " join Material_Title MT on M.id = MT.material\n" +
                " join LanguageString LS on MT.title = LS.id\n" +
                "where M.lang = 1\n" +
                "order by lang desc;")
                .getResultList();
    }
}

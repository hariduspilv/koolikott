package ee.hm.dop.dao;

import ee.hm.dop.model.LanguageString;
import ee.hm.dop.model.MaterialTitle;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MaterialTitleDao extends AbstractDao<LanguageString> {

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

    public List<MaterialTitle> findAllMaterialTitles() {
        List<MaterialTitle> materialTitleList = new ArrayList<>();
        List<Object[]> resultList = getEntityManager()
                .createNativeQuery(
                "select LS.textValue as lang, M.id,LO.updated,LO.added\n" +
                        " from LearningObject LO\n" +
                        " join Material M on LO.id = M.id\n" +
                        " join Material_Title MT on M.id = MT.material\n" +
                        " join LanguageString LS on MT.title = LS.id\n" +
                        " where M.lang in (1,2,3) AND LO.deleted = 0\n" +
                        " order by lang desc;")
                .getResultList();

        for (Object[] objects : resultList) {
            materialTitleList.add(new MaterialTitle(String.valueOf(objects[0]), ((BigInteger) objects[1]).longValue(),objects[2] == null ? String.valueOf(objects[3]): String.valueOf(objects[2])));
        }
        return materialTitleList;
    }
}

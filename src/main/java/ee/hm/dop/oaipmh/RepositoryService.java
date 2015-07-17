package ee.hm.dop.oaipmh;

/**
 * Created by mart.laus on 14.07.2015.
 */
public class RepositoryService {
    public String url = "http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler";

    //update one repository, currently Waramu
    public void updateRepository() throws Exception {
        RepositoryManager repositoryManager = new RepositoryManager();
        repositoryManager.getMaterials(url);
    }
}

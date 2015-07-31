package ee.hm.dop.rest;

import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.hm.dop.model.Repository;
import ee.hm.dop.service.RepositoryService;

/**
 * Created by mart.laus on 22.07.2015.
 */
@Path("updateResources")
public class RepositoryResource {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryResource.class);
    public String url = "http://koolitaja.eenet.ee:57219/Waramu3Web/OAIHandler";

    @Inject
    private RepositoryService repositoryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateResources() {
        List<Repository> repositories = repositoryService.getAllRepositorys();
        for (Repository repository : repositories) {
                repositoryService.updateRepository(repository);
        }

        return Response.status(HttpURLConnection.HTTP_OK).build();
    }
}

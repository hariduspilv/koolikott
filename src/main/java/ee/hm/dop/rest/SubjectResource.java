package ee.hm.dop.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ee.hm.dop.model.Subject;
import ee.hm.dop.service.SubjectService;

@Path("subject")
public class SubjectResource {

    @Inject
    private SubjectService subjectService;

    @GET
    @Path("getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

}

package ee.hm.dop.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import ee.hm.dop.service.HelloWorldService;

@Path("helloWorld")
public class HelloWorldResource {

    @Inject
    private HelloWorldService helloWorldService;

    @GET
    public String sayHello() {
        return helloWorldService.sayHello();
    }
}

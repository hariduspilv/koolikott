package ee.hm.dop.rest;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

@RestController
public class SitemapResource extends BaseResource {

    @Inject
    private RequestMappingHandlerMapping re;

    @GetMapping(value = "/sitemaps_index.xml",produces = MediaType.APPLICATION_XML_VALUE)
    public File getSitemap() throws IOException {

        File resource = new ClassPathResource("/home/marekr/Proged/spring-back-end/src/main/resources/materials.xml").getFile();

        return resource;
    }
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = re.getHandlerMethods();
//        List<String> urls = new ArrayList<>();
//        for (Map.Entry<RequestMappingInfo,HandlerMethod> entry: handlerMethods.entrySet()){
//            urls.addAll(entry.getKey().getPatternsCondition().getPatterns());
//        }
//
//        return "";
//    }
}

package ee.hm.dop.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("sitemap")
public class SitemapResource extends BaseResource {

    @Inject
    private RequestMappingHandlerMapping re;

    @GetMapping("sitemap.xml")
    public String getSitemap(){
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = re.getHandlerMethods();
        List<String> urls = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo,HandlerMethod> entry: handlerMethods.entrySet()){
            urls.addAll(entry.getKey().getPatternsCondition().getPatterns());
        }

        return "";
    }
}

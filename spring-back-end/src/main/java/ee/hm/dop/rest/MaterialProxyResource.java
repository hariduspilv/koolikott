package ee.hm.dop.rest;

import ee.hm.dop.service.proxy.MaterialProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("material/externalMaterial")
public class MaterialProxyResource extends BaseResource {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MaterialProxy materialProxy;

    @GetMapping
    public ResponseEntity<?> getProxyUrl(@RequestParam("id") Long id, @RequestParam("url") String url_param) throws IOException {
        if (StringUtils.isBlank(url_param) || url_param.equals("undefined")) {
            return materialProxy.noContent();
        }
        try {
            return materialProxy.getProxyUrl(id, url_param);
        } catch (Exception e) {
            logger.error("getProxyUrl caused error for LearningObject {}, url {}, error {}. Returning no content", id, url_param, e.getMessage(), e);
            return materialProxy.noContent();
        }
    }
}

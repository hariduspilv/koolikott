package ee.hm.dop.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionResource {

    @GetMapping
    public String getVersion() {
        return "1.28.1";
    }

}

package ee.hm.dop.rest;

import ee.hm.dop.model.ehis.InstitutionEhis;
import ee.hm.dop.service.ehis.EhisInstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ehisInstitution")
public class EhisInstitutionResource extends BaseResource {

    @Autowired
    private EhisInstitutionService ehisInstitutionService;

    @GetMapping
    @RequestMapping
    public List<InstitutionEhis> getEhisInstitutions(){
        return ehisInstitutionService.findAll();
    }
}

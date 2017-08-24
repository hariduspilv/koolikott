package ee.hm.dop.config.guice.module;

import com.google.inject.servlet.ServletModule;
import ee.hm.dop.config.guice.GuiceInjector.Module;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.CrossCurricularThemeService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LicenseTypeService;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.LogoutService;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.MobileIDLoginService;
import ee.hm.dop.service.MobileIDSOAPService;
import ee.hm.dop.service.PageService;
import ee.hm.dop.service.PublisherService;
import ee.hm.dop.service.RepositoryService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.TaatService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TaxonService;
import ee.hm.dop.service.TranslationService;
import ee.hm.dop.service.UserService;
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder;

@Module
public class RestModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(MaterialService.class);
        bind(TranslationService.class);
        bind(SearchService.class);
        bind(PageService.class);
        bind(LanguageService.class);
        bind(RepositoryService.class);
        bind(TagService.class);
        bind(TaxonService.class);
        bind(ResourceTypeService.class);
        bind(LicenseTypeService.class);
        bind(UserService.class);
        bind(LoginService.class);
        bind(HTTPRedirectDeflateEncoder.class);
        bind(TaatService.class);
        bind(AuthenticatedUserService.class);
        bind(LogoutService.class);
        bind(MobileIDLoginService.class);
        bind(MobileIDSOAPService.class);
        bind(PublisherService.class);
        bind(CrossCurricularThemeService.class);
    }
}

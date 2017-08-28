package ee.hm.dop.config.guice.module;

import com.google.inject.servlet.ServletModule;
import ee.hm.dop.config.guice.GuiceInjector.Module;
import ee.hm.dop.service.useractions.AuthenticatedUserService;
import ee.hm.dop.service.metadata.CrossCurricularThemeService;
import ee.hm.dop.service.metadata.LanguageService;
import ee.hm.dop.service.metadata.LicenseTypeService;
import ee.hm.dop.service.login.LoginService;
import ee.hm.dop.service.login.LogoutService;
import ee.hm.dop.service.content.MaterialService;
import ee.hm.dop.service.login.MobileIDLoginService;
import ee.hm.dop.service.login.MobileIDSOAPService;
import ee.hm.dop.service.content.PageService;
import ee.hm.dop.service.author.PublisherService;
import ee.hm.dop.service.synchronizer.RepositoryService;
import ee.hm.dop.service.metadata.ResourceTypeService;
import ee.hm.dop.service.solr.SearchService;
import ee.hm.dop.service.login.TaatService;
import ee.hm.dop.service.metadata.TagService;
import ee.hm.dop.service.metadata.TaxonService;
import ee.hm.dop.service.metadata.TranslationService;
import ee.hm.dop.service.useractions.UserService;
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

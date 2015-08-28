package ee.hm.dop.guice.module;

import com.google.inject.servlet.ServletModule;

import ee.hm.dop.guice.GuiceInjector.Module;
import ee.hm.dop.service.AuthenticatedUserService;
import ee.hm.dop.service.EducationalContextService;
import ee.hm.dop.service.LanguageService;
import ee.hm.dop.service.LicenseTypeService;
import ee.hm.dop.service.LoginService;
import ee.hm.dop.service.LogoutService;
import ee.hm.dop.service.MaterialService;
import ee.hm.dop.service.PageService;
import ee.hm.dop.service.RepositoryService;
import ee.hm.dop.service.ResourceTypeService;
import ee.hm.dop.service.SearchService;
import ee.hm.dop.service.SubjectService;
import ee.hm.dop.service.TagService;
import ee.hm.dop.service.TranslationService;
import ee.hm.dop.service.UserService;

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
        bind(SubjectService.class);
        bind(EducationalContextService.class);
        bind(ResourceTypeService.class);
        bind(LicenseTypeService.class);
        bind(UserService.class);
        bind(LoginService.class);
        bind(AuthenticatedUserService.class);
        bind(LogoutService.class);
    }
}

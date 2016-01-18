require.config({
  paths: {
    authenticatedUserService: 'services/authenticatedUserService',
    toastService: 'services/toastService',
    serverCallService: 'services/serverCallService',
    authenticationService: 'services/authenticationService',
    searchService: 'services/searchService',
    dopHeader: 'directives/header/header',
    editPortfolioModeHeader: 'directives/editPortfolioModeHeader/editPortfolioModeHeader',
    dopFooter: 'directives/footer/footer',
    dopSidebar: 'directives/sidebar/sidebar',
    translationService: 'services/translationService',
    dopAlert: 'directives/alert/alert',
    alertService: 'services/alertService',
    iconService: 'services/iconService',
    inputValueControl: 'directives/input-value-control',
    dopDetailedSearch: 'directives/detailedSearch/detailedSearch',
    dopMainFabButton: 'directives/mainFabButton/mainFabButton',
    addPortfolioDialogController: 'views/addPortfolioDialog/addPortfolioDialog',
    addMaterialDialogController: 'views/addMaterialDialog/addMaterialDialog',
    loginDialogController: 'views/loginDialog/loginDialog',
    dopTableOfContents: 'directives/tableOfContents/tableOfContents',
    commons: 'utils/commons',
    dopTargetGroupSelector: 'directives/targetGroupSelector/targetGroupSelector',
    dopCommentsCard: 'directives/commentsCard/commentsCard',
    dopRating: 'directives/rating/rating',
    dopAddChapterMaterial: 'directives/chapter/addChapterMaterial/addChapterMaterial',
    dopCopyPermalink: 'directives/copyPermalink/copyPermalink',
    dopReportImproper: 'directives/report/improper/improper',
    dopReportBrokenLink: 'directives/report/brokenLink/brokenLink',
    dopRecommend: 'directives/recommend/recommend',
    angular: '../bower_components/angular/angular',
    'angular-animate': '../bower_components/angular-animate/angular-animate',
    'angular-aria': '../bower_components/angular-aria/angular-aria',
    'angular-click-outside': '../bower_components/angular-click-outside/clickoutside.directive',
    'angular-material': '../bower_components/angular-material/angular-material',
    'angular-resource': '../bower_components/angular-resource/angular-resource',
    'angular-route': '../bower_components/angular-route/angular-route',
    'angular-translate': '../bower_components/angular-translate/angular-translate',
    'angular-translate-loader-url': '../bower_components/angular-translate-loader-url/angular-translate-loader-url',
    'angular-youtube-mb': '../bower_components/angular-youtube-mb/src/angular-youtube-embed',
    requirejs: '../bower_components/requirejs/require',
    dopTaxonSelector: 'directives/taxonSelector/taxonSelector',
    'angular-screenfull': '../bower_components/angular-screenfull/dist/angular-screenfull',
    screenfull: '../bower_components/screenfull/dist/screenfull',
    'angular-scroll': '../bower_components/angular-scroll/angular-scroll',
    jsog: '../bower_components/jsog/lib/JSOG',
    ngInfiniteScroll: '../bower_components/ngInfiniteScroll/build/ng-infinite-scroll',
    jquery: '../bower_components/jquery/dist/jquery',
    'ng-file-upload-shim': '../bower_components/ng-file-upload-shim/ng-file-upload-shim',
    'ng-file-upload': '../bower_components/ng-file-upload/ng-file-upload',
    clipboard: '../bower_components/clipboard/dist/clipboard',
    metadataService: 'services/metadataService',
    linearLayout: 'directives/pageStructure/linearLayout/linearLayout',
    columnLayout: 'directives/pageStructure/columnLayout/columnLayout',
    'angular-material-data-table': '../bower_components/angular-material-data-table/dist/md-data-table'
  },
  shim: {
    angular: {
      exports: 'angular'
    },
    'angular-animate': [
      'angular'
    ],
    'angular-aria': [
      'angular'
    ],
    'angular-material': [
      'angular-animate',
      'angular-aria'
    ],
    'angular-translate': [
      'angular'
    ],
    'angular-translate-loader-url': [
      'angular-translate'
    ],
    'angular-route': [
      'angular'
    ],
    'angular-youtube-mb': [
      'angular'
    ],
    'angular-resource': [
      'angular'
    ],
    'angular-material-icons': [
      'angular'
    ],
    'angular-screenfull': [
      'angular',
      'screenfull'
    ],
    'angular-scroll': [
      'angular'
    ],
    'ng-file-upload-shim': [
      'angular'
    ],
    'ng-file-upload': [
      'angular'
    ],
    'angular-click-outside': [
      'angular'
    ],
    'angular-material-data-table': [
      'angular-material',
      'angular'
    ],
    ngInfiniteScroll: [
      'angular',
      'jquery'
    ],
    app: [
      'angular',
      'angular-translate',
      'angular-translate-loader-url',
      'angular-youtube-mb',
      'commons'
    ],
    priority: [
      'angular'
    ]
  },
  packages: [

  ]
});

require([
  'jquery',
  'angular',
  'app',
  'angular-translate',
  'angular-translate-loader-url',
  'angular-youtube-mb',
  'angular-route',
  'angular-material',
  'angular-screenfull',
  'angular-scroll',
  'angular-click-outside',
  'angular-material-data-table',
  'ng-file-upload-shim',
  'ng-file-upload',
  'clipboard',
  'toastService',
  'translationService',
  'authenticatedUserService',
  'serverCallService',
  'authenticationService',
  'searchService',
  'dopHeader',
  'editPortfolioModeHeader',
  'dopFooter',
  'dopSidebar',
  'dopAlert',
  'alertService',
  'iconService',
  'inputValueControl',
  'dopDetailedSearch',
  'dopMainFabButton',
  'addPortfolioDialogController',
  'addMaterialDialogController',
  'loginDialogController',
  'dopTaxonSelector',
  'dopTableOfContents',
  'jsog',
  'ngInfiniteScroll',
  'dopTargetGroupSelector',
  'dopCommentsCard',
  'dopRating',
  'dopAddChapterMaterial',
  'dopCopyPermalink',
  'dopReportImproper',
  'dopReportBrokenLink',
  'dopRecommend',
  'metadataService',
  'linearLayout',
  'columnLayout'
], function (jquery, angular, app) {
  'use strict';
  var $html = angular.element(document.getElementsByTagName('html')[0]);

  angular.element().ready(function() {
    angular.bootstrap($html, [app.name]);
  });
});
